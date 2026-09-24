package com.walking.listing.application.event;

import java.util.UUID;

public record InventoryExpiredPayload(
        UUID listingId,
        UUID reservationId,
        UUID orderId,
        int remainingQuantity
) {
}
