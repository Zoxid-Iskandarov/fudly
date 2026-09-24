package com.walking.inventory.scheduler;

import com.walking.inventory.application.service.InventoryService;
import com.walking.inventory.domain.entity.reservation.ReservationStatus;
import com.walking.inventory.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationExpirationScheduler {
    private final ReservationRepository reservationRepository;
    private final InventoryService inventoryService;

    @Scheduled(fixedDelayString = "${app.reservation.expiration-delay-ms}")
    public void expireStaleReservations() {
        List<UUID> expiredIds = reservationRepository
                .findIdsByStatusAndExpiresAtBefore(ReservationStatus.ACTIVE, OffsetDateTime.now());

        expiredIds.forEach(inventoryService::expireReservation);
    }
}
