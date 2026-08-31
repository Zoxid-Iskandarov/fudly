package com.walking.listing.domain.exception;

public class ListingNotEditableException extends RuntimeException {
    public ListingNotEditableException(String message) {
        super(message);
    }
}
