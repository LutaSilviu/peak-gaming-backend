package com.peak.gaming.exception;

/** Thrown when a reservation's status can't move directly from its current state to the requested one. */
public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
