package com.walking.listing.application.dto.listing;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ListingPublishedPayload(
        UUID listingId,
        UUID branchId,
        Integer quantity,
        BigDecimal discountedPrice,
        OffsetDateTime expirationTime
) {
}
