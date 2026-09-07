package com.walking.listing.scheduler;

import com.walking.listing.config.AppProperties;
import com.walking.listing.domain.dto.common.EventEnvelope;
import com.walking.listing.domain.entity.outbox.OutboxEvent;
import com.walking.listing.domain.entity.outbox.OutboxStatus;
import com.walking.listing.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, EventEnvelope> kafkaTemplate;
    private final TransactionTemplate transactionalTemplate;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms}")
    public void publishPendingEvents() {
        List<OutboxEvent> events = claimBatch();

        for (OutboxEvent event : events) {
            try {
                EventEnvelope envelope = new EventEnvelope(
                        event.getId(),
                        event.getEventType().getWireName(),
                        event.getAggregateType(),
                        event.getAggregateId(),
                        event.getCreated(),
                        objectMapper.readTree(event.getPayload()));

                kafkaTemplate.send(appProperties.getOutbox().getTopic(), event.getAggregateId().toString(), envelope)
                        .whenComplete((result, e) -> {
                            if (e != null) {
                                log.error("Failed to publish outbox event to Kafka: eventId={}", event.getId(), e);
                                markFailed(event.getId());
                            } else {
                                log.debug("Successfully published outbox event: eventId={}", event.getId());
                                markSent(event.getId());
                            }
                        });
            } catch (Exception e) {
                log.error("Failed to process and enqueue outbox event: eventId={}", event.getId(), e);
                markFailed(event.getId());
            }
        }
    }

    @Scheduled(fixedDelayString = "${app.outbox.requeue-delay-ms}")
    public void requeueExpiredEvents() {
        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(appProperties.getOutbox().getLeaseTimeMinutes());
        Integer count = transactionalTemplate.execute(status ->
                outboxEventRepository.requeueExpired(threshold));
        if (count != null && count > 0) {
            log.debug("Requeued {} expired processing outbox events back to PENDING", count);
        }
    }

    private List<OutboxEvent> claimBatch() {
        return transactionalTemplate.execute(status -> {
            List<OutboxEvent> events = outboxEventRepository.findByPendingAndLock(appProperties.getOutbox().getBatchSize());

            if (events.isEmpty()) return List.of();

            List<UUID> ids = events.stream()
                    .map(OutboxEvent::getId)
                    .toList();

            outboxEventRepository.updateStatusAndAttemptTimeInBatch(OutboxStatus.PROCESSING, OffsetDateTime.now(), ids);

            return events;
        });
    }

    private void markSent(UUID id) {
        transactionalTemplate.executeWithoutResult(status ->
                outboxEventRepository.updateStatusAndSendAt(id, OutboxStatus.SENT, OffsetDateTime.now()));
    }

    private void markFailed(UUID id) {
        transactionalTemplate.executeWithoutResult(status ->
                outboxEventRepository.updateStatus(id, OutboxStatus.FAILED));
    }
}
