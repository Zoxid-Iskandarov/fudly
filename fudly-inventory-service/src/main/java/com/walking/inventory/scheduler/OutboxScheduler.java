package com.walking.inventory.scheduler;

import com.walking.inventory.application.kafka.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxEventPublisher outboxEventPublisher;

    @Scheduled(fixedDelayString = "${app.kafka.outbox.fixed-delay-ms}")
    public void publishPendingEvents() {
        outboxEventPublisher.publishPendingEvents();
    }

    @Scheduled(fixedDelayString = "${app.kafka.outbox.requeue-delay-ms}")
    public void requeueExpiredEvents() {
        outboxEventPublisher.requeueExpiredEvents();
    }
}
