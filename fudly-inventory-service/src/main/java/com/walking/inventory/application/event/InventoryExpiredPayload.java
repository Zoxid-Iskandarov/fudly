package com.walking.inventory.application.event;

import java.util.UUID;

public record InventoryExpiredPayload(
        UUID listingId,
        UUID reservationId,
        UUID orderId,
        int remainingQuantity
) {
}
