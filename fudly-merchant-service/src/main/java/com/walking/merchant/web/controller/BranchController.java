package com.walking.merchant.web.controller;

import com.walking.merchant.domain.dto.branch.*;
import com.walking.merchant.service.BranchService;
import com.walking.merchant.service.BranchStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;
    private final BranchStaffService branchStaffService;

    @GetMapping("/{branchId}")
    public BranchResponse getBranchById(@PathVariable UUID branchId) {
        return branchService.getBranchById(branchId);
    }

    @GetMapping("/near")
    public List<BranchNearResponse> findNearByBranches(@Validated GeoSearchRequest geoSearchRequest) {
        return branchService.findNearByBranches(geoSearchRequest);
    }

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public ResponseEntity<BranchResponse> createBranch(
            @RequestBody @Validated CreateBranchRequest createBranchRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(branchService.createBranch(createBranchRequest, jwt));
    }

    @PutMapping("/{branchId}")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public BranchResponse updateBranch(
            @PathVariable UUID branchId,
            @RequestBody @Validated UpdateBranchRequest updateBranchRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return branchService.updateBranch(branchId, updateBranchRequest, jwt);
    }

    @PatchMapping("/{branchId}/status")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public BranchResponse updateBranchStatus(
            @PathVariable UUID branchId,
            @RequestBody @Validated UpdateBranchStatusRequest updateBranchStatusRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return branchService.updateBranchStatus(branchId, updateBranchStatusRequest.status(), jwt);
    }

    @GetMapping("/{branchId}/can-publish")
    public boolean canPublishListing(@PathVariable UUID branchId) {
        return branchService.canPublishListing(branchId);
    }

    @GetMapping("/{branchId}/staff/{userId}")
    public boolean hasAccessToBranch(
            @PathVariable UUID branchId,
            @PathVariable UUID userId) {
        return branchStaffService.hasAccessToBranch(branchId, userId);
    }

    @PutMapping("/{branchId}/staff/{userId}")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public ResponseEntity<Void> assignStaffToBranch(
            @PathVariable UUID branchId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt) {
        branchStaffService.assignStaffToBranch(branchId, userId, jwt);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{branchId}/staff/{userId}")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public ResponseEntity<Void> removeStaffFromBranch(
            @PathVariable UUID branchId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt) {
        branchStaffService.removeStaffFromBranch(branchId, userId, jwt);
        return ResponseEntity.noContent().build();
    }
}
