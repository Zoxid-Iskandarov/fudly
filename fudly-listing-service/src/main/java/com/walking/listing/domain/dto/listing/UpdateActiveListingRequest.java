package com.walking.listing.domain.dto.listing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateActiveListingRequest(
        @NotBlank
        @Size(max = 150)
        String title,

        String description
) {
}
