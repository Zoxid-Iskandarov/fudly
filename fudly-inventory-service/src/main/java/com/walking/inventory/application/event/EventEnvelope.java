package com.walking.inventory.application.event;

import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventEnvelope(
        UUID eventId,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        OffsetDateTime occurredAt,
        JsonNode payload
) {
}
