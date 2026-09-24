package com.walking.inventory.application.event;

import java.util.UUID;

public record ListingCancelledPayload(
        UUID listingId
) {
}
