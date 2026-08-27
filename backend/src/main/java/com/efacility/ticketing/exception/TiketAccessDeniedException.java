package com.efacility.ticketing.exception;

public class TiketAccessDeniedException extends RuntimeException {

    public TiketAccessDeniedException(String message) {
        super(message);
    }
}
