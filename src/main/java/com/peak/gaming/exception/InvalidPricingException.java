package com.peak.gaming.exception;

/** Thrown when the requested station type/duration combination has no price tier. */
public class InvalidPricingException extends RuntimeException {

    public InvalidPricingException(String message) {
        super(message);
    }
}
