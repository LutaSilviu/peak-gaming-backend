package com.peak.gaming.reservation;

import java.math.BigDecimal;
import java.util.List;

/** Mirrors what use-booking-wizard.ts's submit() reads from BOOKING_ENDPOINT: {cod, total, statii}. */
public record CreateReservationResponse(String cod, BigDecimal total, List<String> statii) {

    static CreateReservationResponse from(Reservation r) {
        return new CreateReservationResponse(
                r.getConfirmationCode(),
                r.getTotalPriceLei(),
                r.getStations().stream().map(com.peak.gaming.station.Station::getCode).toList()
        );
    }
}
