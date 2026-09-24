package com.walking.inventory.application.dto.reservation;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record ReserveRequest(
        @NotNull
        UUID listingId,

        @NotNull
        UUID orderId,

        @NotNull
        @Positive
        Integer quantity
) {
}
