package com.peak.gaming.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.peak.gaming.station.Station;
import com.peak.gaming.station.StationRepository;
import com.peak.gaming.station.StationType;
import com.peak.gaming.support.PostgresIntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ReservationRepositoryIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private StationRepository stationRepository;

    private static final LocalDate DATE = LocalDate.of(2031, 3, 10);

    private Reservation bookedReservation(int startHour, int duration, ReservationStatus status) {
        Station station = stationRepository.findByStationTypeOrderByDisplayOrder(StationType.pc).get(0);
        Reservation r = new Reservation();
        r.setConfirmationCode("PK-TST" + startHour + status.ordinal());
        r.setStationType(StationType.pc);
        r.setPeopleCount((short) 1);
        r.setReservationDate(DATE);
        r.setStartHour((short) startHour);
        r.setDurationHours((short) duration);
        r.setCustomerName("Repo Test");
        r.setCustomerPhone("0745000000");
        r.setTotalPriceLei(new BigDecimal("12"));
        r.setStatus(status);
        r.setStations(Set.of(station));
        return reservationRepository.save(r);
    }

    @BeforeEach
    void clean() {
        reservationRepository.deleteAll();
    }

    @Test
    void findsReservationsWhoseIntervalOverlapsTheQueriedWindow() {
        bookedReservation(14, 2, ReservationStatus.noua); // occupies 14:00-16:00

        var overlapping = reservationRepository.findOverlapping(DATE, StationType.pc, 15, 1); // 15:00-16:00

        assertThat(overlapping).hasSize(1);
    }

    @Test
    void doesNotMatchReservationsThatEndExactlyWhenTheQueryStarts() {
        bookedReservation(14, 2, ReservationStatus.noua); // occupies 14:00-16:00

        var adjacent = reservationRepository.findOverlapping(DATE, StationType.pc, 16, 1); // 16:00-17:00

        assertThat(adjacent).isEmpty();
    }

    @Test
    void excludesCancelledReservationsFromOverlapResults() {
        bookedReservation(14, 2, ReservationStatus.anulata); // would occupy 14:00-16:00, but cancelled

        var overlapping = reservationRepository.findOverlapping(DATE, StationType.pc, 14, 1);

        assertThat(overlapping).isEmpty();
    }

    @Test
    void findsByConfirmationCode() {
        Reservation saved = bookedReservation(10, 1, ReservationStatus.noua);

        var found = reservationRepository.findByConfirmationCode(saved.getConfirmationCode());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }
}
