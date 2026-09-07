package com.walking.listing.domain.dto.common;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventEnvelope(
        UUID eventId,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        OffsetDateTime occurredAt,
        Object payload
) {
}
