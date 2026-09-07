package com.walking.listing.domain.entity.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED
}
