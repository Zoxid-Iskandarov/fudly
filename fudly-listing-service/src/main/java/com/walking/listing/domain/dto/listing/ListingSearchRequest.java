package com.walking.listing.domain.dto.listing;

import com.walking.listing.domain.entity.ListingStatus;

import java.util.UUID;

public record ListingSearchRequest(
        UUID branchId,
        ListingStatus status
) {
}
