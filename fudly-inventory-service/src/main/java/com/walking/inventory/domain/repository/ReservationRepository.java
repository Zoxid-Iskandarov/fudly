package com.walking.inventory.domain.repository;

import com.walking.inventory.domain.entity.reservation.Reservation;
import com.walking.inventory.domain.entity.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
            select r from Reservation r
            join fetch r.inventory i
            where i.listingId = :listingId and r.status = :status
            """)
    List<Reservation> findByListingIdAndStatusWithInventory(UUID listingId, ReservationStatus status);

    @Query("select r from Reservation r join fetch r.inventory where r.id = :id")
    Optional<Reservation> findByIdWithInventory(UUID id);

    Optional<Reservation> findByOrderIdAndStatus(UUID orderId, ReservationStatus status);

    @Query("select r.id from Reservation r where r.status = :status and r.expiresAt < :now")
    List<UUID> findIdsByStatusAndExpiresAtBefore(ReservationStatus status, OffsetDateTime now);

    @Modifying
    @Query("""
            update Reservation r
            set r.status = :newStatus
            where r.inventory.listingId = :listingId and r.status = :oldStatus
            """)
    void bulkUpdateStatus(UUID listingId, ReservationStatus oldStatus, ReservationStatus newStatus);
}
