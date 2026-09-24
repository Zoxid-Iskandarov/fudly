package com.walking.listing.application.event;

import java.util.UUID;

public record ListingCancelledPayload(
        UUID listingId
) {
}
