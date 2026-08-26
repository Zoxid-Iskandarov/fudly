package com.walking.merchant.domain.dto.branch;

import com.walking.merchant.domain.entity.branch.OpeningHours;

import java.util.List;
import java.util.UUID;

public record BranchResponse(
        UUID id,
        UUID merchantId,
        String address,
        Double latitude,
        Double longitude,
        List<OpeningHours> openingHours,
        String status
) {
}
