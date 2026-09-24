package com.walking.listing.application.event;

import java.util.UUID;

public record ListingPublishedPayload(
        UUID listingId,
        Integer quantity
) {
}
