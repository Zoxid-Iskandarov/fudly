package com.walking.listing.domain.exception;

public class InvalidListingOperationException extends RuntimeException {
    public InvalidListingOperationException(String message) {
        super(message);
    }
}
