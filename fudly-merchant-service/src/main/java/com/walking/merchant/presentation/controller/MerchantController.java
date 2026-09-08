package com.walking.merchant.presentation.controller;

import com.walking.merchant.application.dto.merchant.MerchantRequest;
import com.walking.merchant.application.dto.merchant.MerchantResponse;
import com.walking.merchant.application.dto.merchant.UpdateMerchantStatusRequest;
import com.walking.merchant.application.service.MerchantService;
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
@RequestMapping("/merchants")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantService merchantService;

    @GetMapping("/{merchantId}")
    public MerchantResponse getMerchantById(@PathVariable UUID merchantId) {
        return merchantService.getMerchantById(merchantId);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public List<MerchantResponse> getMyMerchantsByOwnerId(@AuthenticationPrincipal Jwt jwt) {
        return merchantService.getMerchantsByOwnerId(jwt);
    }

    @PostMapping
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public ResponseEntity<MerchantResponse> createMerchant(
            @RequestBody @Validated MerchantRequest merchantRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(merchantService.createMerchant(merchantRequest, jwt));
    }

    @PutMapping("/{merchantId}")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public MerchantResponse updateMerchant(
            @PathVariable UUID merchantId,
            @RequestBody @Validated MerchantRequest merchantRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return merchantService.updateMerchant(merchantId, merchantRequest, jwt);
    }

    @PatchMapping("/{merchantId}/close")
    @PreAuthorize("hasRole('MERCHANT_OWNER')")
    public MerchantResponse closeMerchant(
            @PathVariable UUID merchantId,
            @AuthenticationPrincipal Jwt jwt) {
        return merchantService.closeMerchant(merchantId, jwt);
    }

    @PatchMapping("/{merchantId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public MerchantResponse updateMerchantStatus(
            @PathVariable UUID merchantId,
            @RequestBody @Validated UpdateMerchantStatusRequest updateMerchantStatusRequest) {
        return merchantService.updateMerchantStatus(merchantId, updateMerchantStatusRequest.status());
    }
}
