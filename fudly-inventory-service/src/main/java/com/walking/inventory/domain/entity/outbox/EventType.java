package com.walking.inventory.domain.entity.outbox;

import lombok.Getter;

@Getter
public enum EventType {
    INVENTORY_RESERVED("InventoryReserved"),
    INVENTORY_RELEASED("InventoryReleased"),
    INVENTORY_CONFIRMED("InventoryConfirmed"),
    INVENTORY_EXPIRED("InventoryExpired");

    private final String wireName;

    EventType(String wireName) {
        this.wireName = wireName;
    }
}
