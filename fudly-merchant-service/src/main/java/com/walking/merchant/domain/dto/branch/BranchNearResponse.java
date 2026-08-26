package com.walking.merchant.domain.dto.branch;

import java.math.BigDecimal;
import java.util.UUID;

public record BranchNearResponse(
        UUID id,
        UUID merchantId,
        String address,
        Double latitude,
        Double longitude,
        String status,
        BigDecimal distanceKm
) {
}
