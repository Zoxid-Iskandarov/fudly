package com.walking.listing.domain.entity.outbox;

import lombok.Getter;

@Getter
public enum EventType {
    LISTING_PUBLISHED("ListingPublished"),
    LISTING_CANCELLED("ListingCancelled");

    private final String wireName;

    EventType(String wireName) {
        this.wireName = wireName;
    }
}
