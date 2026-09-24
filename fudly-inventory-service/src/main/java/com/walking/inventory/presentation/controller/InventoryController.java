package com.walking.inventory.presentation.controller;

import com.walking.inventory.application.dto.reservation.ConfirmRequest;
import com.walking.inventory.application.dto.reservation.ReleaseRequest;
import com.walking.inventory.application.dto.reservation.ReservationResponse;
import com.walking.inventory.application.dto.reservation.ReserveRequest;
import com.walking.inventory.application.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/reserve")
    public ReservationResponse reserve(@RequestBody @Validated ReserveRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/confirm")
    public ReservationResponse confirm(@RequestBody @Validated ConfirmRequest request) {
        return inventoryService.confirm(request);
    }

    @PostMapping("/release")
    public ReservationResponse release(@RequestBody @Validated ReleaseRequest request) {
        return inventoryService.release(request);
    }
}
