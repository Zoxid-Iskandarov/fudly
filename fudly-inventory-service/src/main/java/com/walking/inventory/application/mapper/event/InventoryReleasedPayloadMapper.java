package com.walking.inventory.application.mapper.event;

import com.walking.inventory.application.event.InventoryReleasedPayload;
import com.walking.inventory.domain.entity.inventory.Inventory;
import com.walking.inventory.domain.entity.reservation.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryReleasedPayloadMapper {

    @Mapping(target = "listingId", source = "inventory.listingId")
    @Mapping(target = "reservationId", source = "reservation.id")
    @Mapping(target = "orderId", source = "reservation.orderId")
    @Mapping(target = "remainingQuantity", source = "inventory.availableQuantity")
    InventoryReleasedPayload toPayload(Inventory inventory, Reservation reservation);
}
