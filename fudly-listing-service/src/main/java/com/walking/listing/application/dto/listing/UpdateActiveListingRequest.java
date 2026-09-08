package com.walking.listing.application.dto.listing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateActiveListingRequest(
        @NotBlank
        @Size(max = 150)
        String title,

        String description
) {
}
