package com.walking.inventory.application.dto.reservation;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReleaseRequest(
        @NotNull
        UUID reservationId
) {
}
