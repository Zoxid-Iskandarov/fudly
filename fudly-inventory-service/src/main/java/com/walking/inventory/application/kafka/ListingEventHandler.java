package com.walking.inventory.application.kafka;

import com.walking.inventory.application.event.EventEnvelope;
import com.walking.inventory.application.event.ListingCancelledPayload;
import com.walking.inventory.application.event.ListingPublishedPayload;
import com.walking.inventory.application.mapper.event.InventoryReleasedPayloadMapper;
import com.walking.inventory.application.service.OutboxEventService;
import com.walking.inventory.config.AppProperties;
import com.walking.inventory.domain.entity.inventory.Inventory;
import com.walking.inventory.domain.entity.outbox.EventType;
import com.walking.inventory.domain.entity.processed.ProcessedEvent;
import com.walking.inventory.domain.entity.reservation.Reservation;
import com.walking.inventory.domain.entity.reservation.ReservationStatus;
import com.walking.inventory.domain.repository.InventoryRepository;
import com.walking.inventory.domain.repository.ProcessedEventRepository;
import com.walking.inventory.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingEventHandler {
    private final ProcessedEventRepository processedEventRepository;
    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxEventService outboxEventService;

    private final InventoryReleasedPayloadMapper inventoryReleasedPayloadMapper;

    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    @KafkaListener(topics = "${app.kafka.topics.listing}")
    @Transactional
    public void handle(EventEnvelope envelope) {
        String consumerName = appProperties.getKafka().getConsumers().getListingStatusSync();

        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(envelope.eventId(), consumerName)) {
            log.debug("Event {} already processed, skipping", envelope.eventId());
            return;
        }

        switch (envelope.eventType()) {
            case "ListingPublished" -> handlePublished(envelope.payload());
            case "ListingCancelled" -> handleCancelled(envelope.payload());
            default -> log.debug("Unhandled event type: {}", envelope.eventType());
        }

        processedEventRepository.save(new ProcessedEvent(envelope.eventId(), consumerName));
    }

    private void handlePublished(JsonNode payload) {
        ListingPublishedPayload published = objectMapper.treeToValue(payload, ListingPublishedPayload.class);

        Inventory inventory = new Inventory();
        inventory.setListingId(published.listingId());
        inventory.setAvailableQuantity(published.quantity());
        inventory.setReservedQuantity(0);

        inventoryRepository.save(inventory);
    }

    private void handleCancelled(JsonNode payload) {
        ListingCancelledPayload cancelled = objectMapper.treeToValue(payload, ListingCancelledPayload.class);

        List<Reservation> reservations = reservationRepository.findByListingIdAndStatusWithInventory(
                cancelled.listingId(),
                ReservationStatus.ACTIVE);

        if (reservations.isEmpty()) {
            log.debug("No active reservations for listing {}, nothing to release", cancelled.listingId());
            return;
        }

        Inventory inventory = reservations.getFirst().getInventory();
        int totalReservedToRelease = 0;

        for (Reservation reservation : reservations) {
            totalReservedToRelease += reservation.getQuantity();

            outboxEventService.createOutboxEvent(
                    cancelled.listingId(),
                    EventType.INVENTORY_RELEASED,
                    inventoryReleasedPayloadMapper.toPayload(inventory, reservation));
        }
        reservationRepository.bulkUpdateStatus(cancelled.listingId(), ReservationStatus.ACTIVE, ReservationStatus.RELEASED);

        inventory.setReservedQuantity(inventory.getReservedQuantity() - totalReservedToRelease);
        inventory.setAvailableQuantity(0);
        inventoryRepository.save(inventory);
    }
}
