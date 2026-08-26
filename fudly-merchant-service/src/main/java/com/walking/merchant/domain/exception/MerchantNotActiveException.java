package com.walking.merchant.domain.exception;

public class MerchantNotActiveException extends RuntimeException {
    public MerchantNotActiveException(String message) {
        super(message);
    }
}
