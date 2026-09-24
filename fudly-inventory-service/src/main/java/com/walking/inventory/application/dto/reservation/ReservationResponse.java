package com.walking.inventory.application.dto.reservation;

import com.walking.inventory.domain.entity.reservation.ReservationStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ReservationResponse(
        UUID reservationId,
        ReservationStatus status,
        OffsetDateTime expiresAt
) {
}
