package com.walking.listing.application.dto.listing;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UpdateDraftListingRequest(
        @NotBlank
        @Size(max = 150)
        String title,

        String description,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal originalPrice,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal discountedPrice,

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @Future
        OffsetDateTime expirationTime
) {}
