package com.peak.gaming.reservation;

import com.peak.gaming.station.StationType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Mirrors the frontend's Reservation interface (booking-types.ts) field-for-field. */
public record ReservationDto(
        Long id,
        String cod,
        StationType tip,
        short persoane,
        boolean student,
        LocalDate data,
        short ora,
        short durata,
        String nume,
        String telefon,
        String sursa,
        List<String> statii,
        BigDecimal total,
        ReservationStatus stare,
        long creat
) {

    static ReservationDto from(Reservation r) {
        return new ReservationDto(
                r.getId(),
                r.getConfirmationCode(),
                r.getStationType(),
                r.getPeopleCount(),
                r.isStudentRate(),
                r.getReservationDate(),
                r.getStartHour(),
                r.getDurationHours(),
                r.getCustomerName(),
                r.getCustomerPhone(),
                r.getSource(),
                r.getStations().stream().map(com.peak.gaming.station.Station::getCode).toList(),
                r.getTotalPriceLei(),
                r.getStatus(),
                r.getCreatedAt().toEpochMilli()
        );
    }
}
