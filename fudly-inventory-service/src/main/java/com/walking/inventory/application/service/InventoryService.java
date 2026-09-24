package com.walking.inventory.application.service;

import com.walking.inventory.application.dto.reservation.ConfirmRequest;
import com.walking.inventory.application.dto.reservation.ReleaseRequest;
import com.walking.inventory.application.dto.reservation.ReservationResponse;
import com.walking.inventory.application.dto.reservation.ReserveRequest;
import com.walking.inventory.application.mapper.event.InventoryConfirmedPayloadMapper;
import com.walking.inventory.application.mapper.event.InventoryReleasedPayloadMapper;
import com.walking.inventory.application.mapper.event.InventoryReservedPayloadMapper;
import com.walking.inventory.application.mapper.event.InventoryExpiredPayloadMapper;
import com.walking.inventory.application.mapper.reservation.ReservationResponseMapper;
import com.walking.inventory.config.AppProperties;
import com.walking.inventory.domain.entity.inventory.Inventory;
import com.walking.inventory.domain.entity.outbox.EventType;
import com.walking.inventory.domain.entity.reservation.Reservation;
import com.walking.inventory.domain.entity.reservation.ReservationStatus;
import com.walking.inventory.domain.exception.InsufficientStockException;
import com.walking.inventory.domain.exception.InvalidReservationOperationException;
import com.walking.inventory.domain.exception.ResourceNotFoundException;
import com.walking.inventory.domain.repository.InventoryRepository;
import com.walking.inventory.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxEventService outboxEventService;

    private final AppProperties appProperties;

    private final ReservationResponseMapper reservationResponseMapper;
    private final InventoryReservedPayloadMapper inventoryReservedPayloadMapper;
    private final InventoryConfirmedPayloadMapper inventoryConfirmedPayloadMapper;
    private final InventoryReleasedPayloadMapper inventoryReleasedPayloadMapper;
    private final InventoryExpiredPayloadMapper inventoryExpiredPayloadMapper;

    @Transactional
    public ReservationResponse reserve(ReserveRequest request) {
        Optional<Reservation> existing =
                reservationRepository.findByOrderIdAndStatus(request.orderId(), ReservationStatus.ACTIVE);

        if (existing.isPresent()) {
            log.debug("Reservation for order {} already exists, returning existing", request.orderId());
            return reservationResponseMapper.toDto(existing.get());
        }

        Inventory inventory = inventoryRepository.findById(request.listingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory for listing %s not found".formatted(request.listingId())));

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new InsufficientStockException("Not enough stock for listing %s".formatted(request.listingId()));
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.quantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.quantity());
        inventoryRepository.save(inventory);

        Reservation reservation = reservationRepository.save(Reservation.builder()
                .inventory(inventory)
                .orderId(request.orderId())
                .quantity(request.quantity())
                .status(ReservationStatus.ACTIVE)
                .expiresAt(OffsetDateTime.now().plusMinutes(appProperties.getReservation().getTtlMinutes()))
                .build());

        outboxEventService.createOutboxEvent(
                request.listingId(),
                EventType.INVENTORY_RESERVED,
                inventoryReservedPayloadMapper.toPayload(inventory, reservation));

        return reservationResponseMapper.toDto(reservation);
    }

    @Transactional
    public ReservationResponse confirm(ConfirmRequest request) {
        Reservation reservation = reservationRepository.findByIdWithInventory(request.reservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation %s not found".formatted(request.reservationId())));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new InvalidReservationOperationException("Only ACTIVE reservations can be confirmed");
        }

        Inventory inventory = reservation.getInventory();

        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
        inventoryRepository.save(inventory);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        outboxEventService.createOutboxEvent(
                reservation.getInventory().getListingId(),
                EventType.INVENTORY_CONFIRMED,
                inventoryConfirmedPayloadMapper.toPayload(inventory, reservation));

        return reservationResponseMapper.toDto(reservation);
    }

    @Transactional
    public ReservationResponse release(ReleaseRequest request) {
        Reservation reservation = reservationRepository.findByIdWithInventory(request.reservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation %s not found".formatted(request.reservationId())));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new InvalidReservationOperationException("Only ACTIVE reservations can be released");
        }

        Inventory inventory = reservation.getInventory();
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());

        reservation.setStatus(ReservationStatus.RELEASED);

        outboxEventService.createOutboxEvent(inventory.getListingId(),
                EventType.INVENTORY_RELEASED,
                inventoryReleasedPayloadMapper.toPayload(inventory, reservation));

        return reservationResponseMapper.toDto(reservation);
    }

    @Transactional
    public void expireReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findByIdWithInventory(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation %s not found".formatted(reservationId)));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            return;
        }

        Inventory inventory = reservation.getInventory();
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());

        reservation.setStatus(ReservationStatus.EXPIRED);

        outboxEventService.createOutboxEvent(
                inventory.getListingId(),
                EventType.INVENTORY_EXPIRED,
                inventoryExpiredPayloadMapper.toPayload(inventory, reservation));
    }
}
