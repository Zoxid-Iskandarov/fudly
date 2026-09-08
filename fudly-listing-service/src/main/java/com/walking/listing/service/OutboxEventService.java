package com.walking.listing.service;

import com.walking.listing.domain.entity.outbox.EventType;
import com.walking.listing.domain.entity.outbox.OutboxEvent;
import com.walking.listing.domain.entity.outbox.OutboxStatus;
import com.walking.listing.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void createOutboxEvent(UUID aggregateId, EventType eventType, Object payload) {
        outboxEventRepository.save(OutboxEvent.builder()
                .aggregateType("Listing")
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(objectMapper.writeValueAsString(payload))
                .status(OutboxStatus.PENDING)
                .build());
    }
}
