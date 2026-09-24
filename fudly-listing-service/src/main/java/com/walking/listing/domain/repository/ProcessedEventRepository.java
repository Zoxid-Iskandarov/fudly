package com.walking.listing.domain.repository;

import com.walking.listing.domain.entity.processed.ProcessedEvent;
import com.walking.listing.domain.entity.processed.ProcessedEventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEventId> {

    boolean existsByIdEventIdAndIdConsumerName(UUID eventId, String consumerName);
}
