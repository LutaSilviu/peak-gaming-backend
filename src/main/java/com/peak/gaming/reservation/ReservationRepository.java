package com.peak.gaming.reservation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByReservationDateOrderByStartHourAsc(LocalDate date);

    Optional<Reservation> findByConfirmationCode(String confirmationCode);

    /**
     * Reservations for a given day/station-type whose interval overlaps
     * [startHour, startHour + durationHours), excluding cancelled ones.
     * Mirrors booking-availability.ts's occupiedStations overlap test:
     * max(existing.start, query.start) < min(existing.end, query.end).
     */
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.reservationDate = :date
              AND r.stationType = :stationType
              AND r.status <> com.peak.gaming.reservation.ReservationStatus.anulata
              AND GREATEST(r.startHour, :startHour) < LEAST(r.startHour + r.durationHours, :startHour + :durationHours)
            """)
    List<Reservation> findOverlapping(
            @Param("date") LocalDate date,
            @Param("stationType") com.peak.gaming.station.StationType stationType,
            @Param("startHour") int startHour,
            @Param("durationHours") int durationHours);
}
