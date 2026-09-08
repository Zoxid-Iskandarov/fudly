package com.walking.listing.application.dto.listing;

import com.walking.listing.domain.entity.listing.ListingStatus;

import java.util.UUID;

public record ListingSearchRequest(
        UUID branchId,
        ListingStatus status
) {
}
