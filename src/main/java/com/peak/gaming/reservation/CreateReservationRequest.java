package com.peak.gaming.reservation;

import com.peak.gaming.station.StationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** Mirrors the payload built by use-booking-wizard.ts's submit(). */
@Schema(name = "CreateReservationRequest")
public record CreateReservationRequest(

        @NotNull
        @Schema(description = "Station type", example = "pc")
        StationType tip,

        @NotNull
        @Min(1)
        @Schema(description = "Number of people", example = "2")
        Short persoane,

        @NotNull
        @Schema(description = "Whether the student discount applies (PS5/volan only)")
        Boolean student,

        @NotNull
        @Schema(description = "Reservation date, ISO-8601", example = "2026-09-20")
        java.time.LocalDate data,

        @NotNull
        @Min(0)
        @Max(23)
        @Schema(description = "Start hour, 0-23", example = "14")
        Short ora,

        @NotNull
        @Min(1)
        @Schema(description = "Duration in hours", example = "2")
        Short durata,

        @NotBlank
        @Schema(description = "Customer name", example = "Andrei Popescu")
        String nume,

        @NotBlank
        @Pattern(regexp = "^(\\+?4)?0?7\\d{2}[ .-]?\\d{3}[ .-]?\\d{3}$", message = "must be a valid Romanian phone number")
        @Schema(description = "Customer phone, Romanian format", example = "0745123456")
        String telefon,

        @Schema(description = "Where the booking came from", example = "website")
        String sursa
) {
}
