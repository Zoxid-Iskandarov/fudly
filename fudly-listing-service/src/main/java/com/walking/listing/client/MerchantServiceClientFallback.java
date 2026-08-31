package com.walking.listing.client;

import com.walking.listing.domain.exception.MerchantServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MerchantServiceClientFallback implements MerchantServiceClient {

    @Override
    public boolean hasAccess(UUID branchId, UUID userId) {
        log.error("Merchant service unavailable. Cannot verify access for user {} to branch {}", userId, branchId);
        throw new MerchantServiceUnavailableException("Cannot verify access: merchant-service is unavailable");
    }

    @Override
    public boolean canPublishListing(UUID branchId) {
        log.error("Merchant service unavailable. Cannot check if branch {} is active", branchId);
        throw new MerchantServiceUnavailableException("Cannot check branch status: merchant-service is unavailable");
    }
}
