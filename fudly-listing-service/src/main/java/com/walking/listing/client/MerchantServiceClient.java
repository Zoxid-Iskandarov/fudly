package com.walking.listing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "fudly-merchant-service", path = "/branches", fallback = MerchantServiceClientFallback.class)
public interface MerchantServiceClient {

    @GetMapping("/{branchId}/staff/{userId}")
    boolean hasAccess(@PathVariable UUID branchId, @PathVariable UUID userId);

    @GetMapping("/{branchId}/can-publish")
    boolean canPublishListing(@PathVariable UUID branchId);
}
