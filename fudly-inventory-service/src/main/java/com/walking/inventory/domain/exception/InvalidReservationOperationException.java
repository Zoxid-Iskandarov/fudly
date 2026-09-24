package com.walking.inventory.domain.exception;

public class InvalidReservationOperationException extends RuntimeException {
    public InvalidReservationOperationException(String message) {
        super(message);
    }
}
