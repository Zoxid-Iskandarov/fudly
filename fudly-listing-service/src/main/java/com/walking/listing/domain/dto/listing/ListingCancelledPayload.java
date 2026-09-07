package com.walking.listing.domain.dto.listing;

import java.util.UUID;

public record ListingCancelledPayload(
        UUID listingId,
        UUID branchId
) {
}
