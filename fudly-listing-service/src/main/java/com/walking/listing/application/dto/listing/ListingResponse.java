package com.walking.listing.application.dto.listing;

import com.walking.listing.domain.entity.listing.ListingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ListingResponse(
        UUID id,
        UUID branchId,
        String title,
        String description,
        BigDecimal originalPrice,
        BigDecimal discountedPrice,
        Integer quantity,
        OffsetDateTime expirationTime,
        ListingStatus status,
        OffsetDateTime created,
        OffsetDateTime updated
) {
}
