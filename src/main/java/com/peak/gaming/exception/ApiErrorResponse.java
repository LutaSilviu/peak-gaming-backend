package com.peak.gaming.exception;

import java.time.Instant;
import java.util.List;

/** `eroare` mirrors the field name use-booking-wizard.ts reads from a failed BOOKING_ENDPOINT response. */
public record ApiErrorResponse(
        Instant timestamp, int status, String error, String message, String eroare, List<String> details) {

    public static ApiErrorResponse of(int status, String error, String message) {
        return new ApiErrorResponse(Instant.now(), status, error, message, message, List.of());
    }

    public static ApiErrorResponse of(int status, String error, String message, List<String> details) {
        return new ApiErrorResponse(Instant.now(), status, error, message, message, details);
    }
}
