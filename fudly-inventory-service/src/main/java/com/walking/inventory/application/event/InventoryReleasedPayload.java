package com.walking.inventory.application.event;

import java.util.UUID;

public record InventoryReleasedPayload(
        UUID listingId,
        UUID reservationId,
        UUID orderId,
        int remainingQuantity
) {
}
