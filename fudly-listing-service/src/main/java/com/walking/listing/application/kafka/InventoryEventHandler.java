package com.walking.listing.application.kafka;

import com.walking.listing.application.event.*;
import com.walking.listing.config.AppProperties;
import com.walking.listing.domain.entity.processed.ProcessedEvent;
import com.walking.listing.domain.entity.listing.Listing;
import com.walking.listing.domain.entity.listing.ListingStatus;
import com.walking.listing.domain.repository.ListingRepository;
import com.walking.listing.domain.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventHandler {

    private final ProcessedEventRepository processedEventRepository;
    private final ListingRepository listingRepository;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    @KafkaListener(topics = "${app.kafka.topics.inventory}")
    @Transactional
    public void handle(EventEnvelope envelope) {
        String consumerName = appProperties.getKafka().getConsumers().getInventoryStatusSync();

        if (processedEventRepository.existsByIdEventIdAndIdConsumerName(envelope.eventId(), consumerName)) {
            log.debug("Event {} already processed, skipping", envelope.eventId());
            return;
        }

        switch (envelope.eventType()) {
            case "InventoryReserved" -> handleReserved(envelope.payload());
            case "InventoryReleased" -> handleReleased(envelope.payload());
            case "InventoryExpired" -> handleExpired(envelope.payload());
            case "InventoryConfirmed" -> handleConfirmed(envelope.payload());
            default -> log.debug("Unhandled event type: {}", envelope.eventType());
        }

        processedEventRepository.save(new ProcessedEvent(envelope.eventId(), consumerName));
    }

    private void handleReserved(JsonNode payload) {
        InventoryReservedPayload reserved = objectMapper.treeToValue(payload, InventoryReservedPayload.class);

        applyUpdate(reserved.listingId(), reserved.remainingQuantity(), listing -> {
            if (listing.getStatus() != ListingStatus.ACTIVE) {
                return false;
            }
            listing.setStatus(reserved.remainingQuantity() == 0 ? ListingStatus.RESERVED : ListingStatus.ACTIVE);
            return true;
        });
    }

    private void handleReleased(JsonNode payload) {
        InventoryReleasedPayload released = objectMapper.treeToValue(payload, InventoryReleasedPayload.class);

        applyUpdate(released.listingId(), released.remainingQuantity(), listing -> {
            if (listing.getStatus() != ListingStatus.RESERVED) {
                return false;
            }
            listing.setStatus(ListingStatus.ACTIVE);
            return true;
        });
    }

    private void handleExpired(JsonNode payload) {
        InventoryExpiredPayload expired = objectMapper.treeToValue(payload, InventoryExpiredPayload.class);

        applyUpdate(expired.listingId(), expired.remainingQuantity(), listing -> {
            if (listing.getStatus() != ListingStatus.RESERVED) {
                return false;
            }
            listing.setStatus(ListingStatus.ACTIVE);
            return true;
        });
    }

    private void handleConfirmed(JsonNode payload) {
        InventoryConfirmedPayload confirmed = objectMapper.treeToValue(payload, InventoryConfirmedPayload.class);

        applyUpdate(confirmed.listingId(), confirmed.remainingQuantity(), listing -> {
            if (listing.getStatus() != ListingStatus.RESERVED) {
                return false;
            }
            listing.setStatus(confirmed.remainingQuantity() == 0 ? ListingStatus.SOLD_OUT : ListingStatus.ACTIVE);
            return true;
        });
    }

    private void applyUpdate(UUID listingId, int remainingQuantity, Predicate<Listing> transition) {
        listingRepository.findById(listingId).ifPresentOrElse(listing -> {
            if (isTerminal(listing.getStatus())) {
                log.debug("Listing {} is in terminal status {}, ignoring inventory event", listingId, listing.getStatus());
                return;
            }

            listing.setQuantity(remainingQuantity);
            boolean statusChanged = transition.test(listing);

            if (!statusChanged) {
                log.warn("Listing {} has unexpected status {} for this inventory event", listingId, listing.getStatus());
            }

            listingRepository.save(listing);
        }, () -> log.warn("Listing {} not found for inventory event", listingId));
    }

    private boolean isTerminal(ListingStatus status) {
        return status == ListingStatus.CANCELLED
                || status == ListingStatus.EXPIRED
                || status == ListingStatus.SOLD_OUT;
    }
}
