package com.walking.inventory.application.mapper.event;

import com.walking.inventory.application.event.EventEnvelope;
import com.walking.inventory.domain.entity.outbox.OutboxEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import tools.jackson.databind.JsonNode;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventEnvelopeMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventType", source = "event.eventType.wireName")
    @Mapping(target = "occurredAt", source = "event.created")
    @Mapping(target = "payload", source = "payload")
    EventEnvelope toEnvelope(OutboxEvent event, JsonNode payload);
}
