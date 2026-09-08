package com.walking.listing.scheduler;

import com.walking.listing.application.service.OutboxPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxPublisherService outboxPublisherService;

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay-ms}")
    public void publishPendingEvents() {
        outboxPublisherService.publishPendingEvents();
    }

    @Scheduled(fixedDelayString = "${app.outbox.requeue-delay-ms}")
    public void requeueExpiredEvents() {
        outboxPublisherService.requeueExpiredEvents();
    }
}
