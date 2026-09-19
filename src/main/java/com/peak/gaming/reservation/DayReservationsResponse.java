package com.peak.gaming.reservation;

import java.util.List;

/** Mirrors what use-booking-wizard.ts reads from SLOTS_ENDPOINT: {rezervari: [...]}. */
public record DayReservationsResponse(List<ReservationDto> rezervari) {

    static DayReservationsResponse from(List<Reservation> reservations) {
        return new DayReservationsResponse(reservations.stream().map(ReservationDto::from).toList());
    }
}
