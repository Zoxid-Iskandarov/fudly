package com.walking.listing.repository;

import com.walking.listing.domain.entity.outbox.OutboxEvent;
import com.walking.listing.domain.entity.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
            SELECT * FROM outbox_event
            WHERE status = 'PENDING'
            ORDER BY created
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEvent> findByPendingAndLock(@Param("limit") int limit);

    @Modifying
    @Query("update OutboxEvent oe set oe.status = :status, oe.lastAttemptAt = :lastAttemptAt where oe.id in :ids")
    void updateStatusAndAttemptTimeInBatch(
            @Param("status") OutboxStatus status,
            @Param("lastAttemptAt") OffsetDateTime lastAttemptAt,
            @Param("ids") List<UUID> ids);

    @Modifying
    @Query("update OutboxEvent oe set oe.status = :status, oe.sentAt = :sentAt where oe.id = :id")
    void updateStatusAndSendAt(
            @Param("id") UUID id,
            @Param("status") OutboxStatus status,
            @Param("sentAt") OffsetDateTime sentAt);

    @Modifying
    @Query("update OutboxEvent oe set oe.status = :status where oe.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") OutboxStatus status);

    @Modifying
    @Query("""
            update OutboxEvent oe
            set oe.status = 'PENDING'
            where oe.status = 'PROCESSING'
                and oe.lastAttemptAt < :threshold
            """)
    int requeueExpired(@Param("threshold") OffsetDateTime threshold);
}
