package com.peak.gaming.reservation;

import com.peak.gaming.exception.InvalidPricingException;
import com.peak.gaming.exception.NoAvailabilityException;
import com.peak.gaming.exception.NotFoundException;
import com.peak.gaming.pricing.PricingService;
import com.peak.gaming.station.Station;
import com.peak.gaming.station.StationRepository;
import com.peak.gaming.station.StationType;
import com.peak.gaming.station.StationTypeEntity;
import com.peak.gaming.station.StationTypeRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StationRepository stationRepository;
    private final StationTypeRepository stationTypeRepository;
    private final PricingService pricingService;
    private final ConfirmationCodeGenerator codeGenerator;
    private final EntityManager entityManager;

    public ReservationService(
            ReservationRepository reservationRepository,
            StationRepository stationRepository,
            StationTypeRepository stationTypeRepository,
            PricingService pricingService,
            ConfirmationCodeGenerator codeGenerator,
            EntityManager entityManager) {
        this.reservationRepository = reservationRepository;
        this.stationRepository = stationRepository;
        this.stationTypeRepository = stationTypeRepository;
        this.pricingService = pricingService;
        this.codeGenerator = codeGenerator;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public DayReservationsResponse listByDate(LocalDate date) {
        return DayReservationsResponse.from(reservationRepository.findByReservationDateOrderByStartHourAsc(date));
    }

    @Transactional(readOnly = true)
    public DayStatsDto statsForDate(LocalDate date) {
        List<Reservation> all = reservationRepository.findByReservationDateOrderByStartHourAsc(date);
        List<Reservation> active = all.stream().filter(r -> r.getStatus() != ReservationStatus.anulata).toList();

        int peopleExpected = active.stream().mapToInt(Reservation::getPeopleCount).sum();
        int stationHours = active.stream()
                .mapToInt(r -> r.getDurationHours() * r.getStations().size())
                .sum();
        BigDecimal revenue = active.stream()
                .map(Reservation::getTotalPriceLei)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DayStatsDto(active.size(), peopleExpected, stationHours, revenue, all.size() - active.size());
    }

    /**
     * Creates a reservation, recomputing the price server-side and assigning
     * free stations. Serialized per (date, stationType) via a Postgres
     * advisory lock so two concurrent requests for the same slot can't both
     * see the same free stations and overbook them.
     */
    @Transactional
    public CreateReservationResponse create(CreateReservationRequest request) {
        acquireAdvisoryLock(request.data(), request.tip());

        StationTypeEntity stationType = stationTypeRepository.findById(request.tip())
                .orElseThrow(() -> new InvalidPricingException("Unknown station type: " + request.tip()));

        int units = pricingService.unitsFor(request.tip(), request.persoane(), stationType.getPeoplePerUnit());

        BigDecimal total = pricingService
                .totalPrice(request.tip(), request.durata(), (int) request.ora(), request.persoane(),
                        Boolean.TRUE.equals(request.student()), stationType.getPeoplePerUnit())
                .orElseThrow(() -> new InvalidPricingException(
                        "No price tier for " + request.tip() + " / " + request.durata() + "h"));

        List<Station> candidates = stationRepository.findByStationTypeOrderByDisplayOrder(request.tip());
        Set<Long> occupiedIds = reservationRepository
                .findOverlapping(request.data(), request.tip(), request.ora(), request.durata())
                .stream()
                .flatMap(r -> r.getStations().stream())
                .map(Station::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<Station> free = candidates.stream().filter(s -> !occupiedIds.contains(s.getId())).toList();
        if (free.size() < units) {
            throw new NoAvailabilityException(
                    "Only " + free.size() + " station(s) free for " + request.tip() + " at " + request.ora() + ":00");
        }

        Set<Station> assigned = new LinkedHashSet<>(free.subList(0, units));

        Reservation reservation = new Reservation();
        reservation.setConfirmationCode(nextUniqueCode());
        reservation.setStationType(request.tip());
        reservation.setPeopleCount(request.persoane());
        reservation.setStudentRate(Boolean.TRUE.equals(request.student()));
        reservation.setReservationDate(request.data());
        reservation.setStartHour(request.ora());
        reservation.setDurationHours(request.durata());
        reservation.setCustomerName(request.nume());
        reservation.setCustomerPhone(request.telefon());
        reservation.setSource(request.sursa() == null || request.sursa().isBlank() ? "website" : request.sursa());
        reservation.setTotalPriceLei(total);
        reservation.setStatus(ReservationStatus.noua);
        reservation.setStations(assigned);

        reservationRepository.save(reservation);
        return CreateReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationDto updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation " + id + " not found"));
        reservation.setStatus(status);
        return ReservationDto.from(reservation);
    }

    private String nextUniqueCode() {
        String code;
        do {
            code = codeGenerator.generate();
        } while (reservationRepository.findByConfirmationCode(code).isPresent());
        return code;
    }

    private void acquireAdvisoryLock(LocalDate date, StationType stationType) {
        long key = (date.toEpochDay() * 31L) + stationType.ordinal();
        entityManager.createNativeQuery("SELECT pg_advisory_xact_lock(:key)")
                .setParameter("key", key)
                .getSingleResult();
    }
}
