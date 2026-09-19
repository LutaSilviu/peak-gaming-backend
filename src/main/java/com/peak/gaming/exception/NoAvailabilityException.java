package com.peak.gaming.exception;

/** Thrown when a reservation request can't be satisfied because not enough stations are free. */
public class NoAvailabilityException extends RuntimeException {

    public NoAvailabilityException(String message) {
        super(message);
    }
}
