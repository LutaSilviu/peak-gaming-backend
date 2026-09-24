package com.peak.gaming.exception;

/** Thrown when a reservation's date/hour/duration falls outside what can ever be booked (past, or outside opening hours). */
public class InvalidBookingWindowException extends RuntimeException {

    public InvalidBookingWindowException(String message) {
        super(message);
    }
}
