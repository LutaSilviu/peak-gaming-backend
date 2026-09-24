package com.peak.gaming.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.peak.gaming.config.AppProperties;
import com.peak.gaming.exception.InvalidBookingWindowException;
import com.peak.gaming.exception.InvalidPricingException;
import com.peak.gaming.exception.InvalidStatusTransitionException;
import com.peak.gaming.exception.NoAvailabilityException;
import com.peak.gaming.exception.NotFoundException;
import com.peak.gaming.pricing.PricingService;
import com.peak.gaming.station.Station;
import com.peak.gaming.station.StationRepository;
import com.peak.gaming.station.StationType;
import com.peak.gaming.station.StationTypeEntity;
import com.peak.gaming.station.StationTypeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private StationTypeRepository stationTypeRepository;
    @Mock
    private PricingService pricingService;
    @Mock
    private ConfirmationCodeGenerator codeGenerator;
    @Mock
    private EntityManager entityManager;
    @Mock
    private Query advisoryLockQuery;

    private ReservationService service;

    /** A date far enough in the future to never become "the past" relative to test-run time. */
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusDays(30);

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties(
                new AppProperties.Admin("test-key"),
                new AppProperties.Cors("http://localhost:3000"),
                new AppProperties.Booking(12, 24));
        service = new ReservationService(
                reservationRepository, stationRepository, stationTypeRepository,
                pricingService, codeGenerator, entityManager, appProperties);
    }

    /** Only tests that reach ReservationService#create() take the advisory-lock path. */
    private void stubAdvisoryLock() {
        when(entityManager.createNativeQuery(any(String.class))).thenReturn(advisoryLockQuery);
        when(advisoryLockQuery.setParameter(any(String.class), any())).thenReturn(advisoryLockQuery);
    }

    private StationTypeEntity stationType(StationType code, short peoplePerUnit) {
        StationTypeEntity e = new StationTypeEntity();
        e.setCode(code);
        e.setPeoplePerUnit(peoplePerUnit);
        e.setMaxPeople((short) 10);
        return e;
    }

    private Station station(long id, String code, StationType type) {
        Station s = new Station();
        s.setId(id);
        s.setCode(code);
        s.setStationType(type);
        s.setDisplayOrder((short) id);
        return s;
    }

    private CreateReservationRequest request(short people, boolean student) {
        return new CreateReservationRequest(
                StationType.pc, people, student, FUTURE_DATE,
                (short) 14, (short) 2, "Andrei Popescu", "0745123456", "website");
    }

    @Test
    void createsReservationAndAssignsFreeStations() {
        stubAdvisoryLock();
        when(stationTypeRepository.findById(StationType.pc)).thenReturn(Optional.of(stationType(StationType.pc, (short) 1)));
        when(pricingService.unitsFor(StationType.pc, 2, (short) 1)).thenReturn(2);
        when(pricingService.totalPrice(eq(StationType.pc), eq((short) 2), eq(14), eq(2), eq(false), eq((short) 1)))
                .thenReturn(Optional.of(new BigDecimal("44")));
        when(stationRepository.findByStationTypeOrderByDisplayOrder(StationType.pc))
                .thenReturn(List.of(station(1, "PC1", StationType.pc), station(2, "PC2", StationType.pc)));
        when(reservationRepository.findOverlapping(any(), eq(StationType.pc), anyInt(), anyInt())).thenReturn(List.of());
        when(codeGenerator.generate()).thenReturn("PK-ABC12");
        when(reservationRepository.findByConfirmationCode("PK-ABC12")).thenReturn(Optional.empty());

        CreateReservationResponse response = service.create(request((short) 2, false));

        assertThat(response.cod()).isEqualTo("PK-ABC12");
        assertThat(response.total()).isEqualByComparingTo("44");
        assertThat(response.statii()).containsExactly("PC1", "PC2");
    }

    @Test
    void rejectsBookingWhenNotEnoughStationsAreFree() {
        stubAdvisoryLock();
        when(stationTypeRepository.findById(StationType.pc)).thenReturn(Optional.of(stationType(StationType.pc, (short) 1)));
        when(pricingService.unitsFor(StationType.pc, 2, (short) 1)).thenReturn(2);
        when(pricingService.totalPrice(eq(StationType.pc), eq((short) 2), eq(14), eq(2), eq(false), eq((short) 1)))
                .thenReturn(Optional.of(new BigDecimal("44")));
        Station onlyFreeStation = station(1, "PC1", StationType.pc);
        Station occupiedStation = station(2, "PC2", StationType.pc);
        when(stationRepository.findByStationTypeOrderByDisplayOrder(StationType.pc))
                .thenReturn(List.of(onlyFreeStation, occupiedStation));

        Reservation existing = new Reservation();
        existing.setStations(java.util.Set.of(occupiedStation));
        when(reservationRepository.findOverlapping(any(), eq(StationType.pc), anyInt(), anyInt()))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.create(request((short) 2, false)))
                .isInstanceOf(NoAvailabilityException.class);
    }

    @Test
    void rejectsBookingWhenNoPriceTierMatches() {
        stubAdvisoryLock();
        when(stationTypeRepository.findById(StationType.pc)).thenReturn(Optional.of(stationType(StationType.pc, (short) 1)));
        when(pricingService.unitsFor(StationType.pc, 2, (short) 1)).thenReturn(2);
        when(pricingService.totalPrice(eq(StationType.pc), eq((short) 2), eq(14), eq(2), eq(false), eq((short) 1)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request((short) 2, false)))
                .isInstanceOf(InvalidPricingException.class);
    }

    @Test
    void rejectsUnknownStationType() {
        // No stubAdvisoryLock(): station-type lookup now happens before the
        // advisory lock is acquired, so this never reaches that code path.
        when(stationTypeRepository.findById(StationType.pc)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(request((short) 2, false)))
                .isInstanceOf(InvalidPricingException.class);
    }

    @Test
    void rejectsReservationDateInThePast() {
        CreateReservationRequest pastRequest = new CreateReservationRequest(
                StationType.pc, (short) 2, false, LocalDate.now().minusDays(1),
                (short) 14, (short) 2, "Andrei Popescu", "0745123456", "website");

        assertThatThrownBy(() -> service.create(pastRequest))
                .isInstanceOf(InvalidBookingWindowException.class);
    }

    @Test
    void rejectsStartHourBeforeOpening() {
        CreateReservationRequest tooEarly = new CreateReservationRequest(
                StationType.pc, (short) 2, false, FUTURE_DATE,
                (short) 8, (short) 2, "Andrei Popescu", "0745123456", "website");

        assertThatThrownBy(() -> service.create(tooEarly))
                .isInstanceOf(InvalidBookingWindowException.class);
    }

    @Test
    void rejectsIntervalExtendingPastClosing() {
        CreateReservationRequest tooLate = new CreateReservationRequest(
                StationType.pc, (short) 2, false, FUTURE_DATE,
                (short) 23, (short) 3, "Andrei Popescu", "0745123456", "website");

        assertThatThrownBy(() -> service.create(tooLate))
                .isInstanceOf(InvalidBookingWindowException.class);
    }

    @Test
    void updatesStatusOfExistingReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.noua);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        ReservationDto result = service.updateStatus(1L, ReservationStatus.confirmata);

        assertThat(result.stare()).isEqualTo(ReservationStatus.confirmata);
    }

    @Test
    void rejectsConfirmingAReservationThatWasAlreadyCancelled() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.anulata);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.updateStatus(1L, ReservationStatus.confirmata))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void throwsNotFoundForUnknownReservationId() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(99L, ReservationStatus.anulata))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void statsExcludeCancelledReservationsFromCounts() {
        Reservation active = new Reservation();
        active.setStatus(ReservationStatus.noua);
        active.setPeopleCount((short) 2);
        active.setDurationHours((short) 2);
        active.setTotalPriceLei(new BigDecimal("44"));
        active.setStations(java.util.Set.of(station(1, "PC1", StationType.pc), station(2, "PC2", StationType.pc)));

        Reservation cancelled = new Reservation();
        cancelled.setStatus(ReservationStatus.anulata);
        cancelled.setPeopleCount((short) 1);
        cancelled.setDurationHours((short) 1);
        cancelled.setTotalPriceLei(new BigDecimal("12"));
        cancelled.setStations(java.util.Set.of(station(3, "PC3", StationType.pc)));

        when(reservationRepository.findByReservationDateOrderByStartHourAsc(LocalDate.of(2026, 9, 20)))
                .thenReturn(List.of(active, cancelled));

        DayStatsDto stats = service.statsForDate(LocalDate.of(2026, 9, 20));

        assertThat(stats.total()).isEqualTo(1);
        assertThat(stats.peopleExpected()).isEqualTo(2);
        assertThat(stats.stationHours()).isEqualTo(4);
        assertThat(stats.estimatedRevenue()).isEqualByComparingTo("44");
        assertThat(stats.cancelled()).isEqualTo(1);
    }
}
