package com.walking.listing.application.dto.listing;

import java.util.UUID;

public record ListingCancelledPayload(
        UUID listingId,
        UUID branchId
) {
}
