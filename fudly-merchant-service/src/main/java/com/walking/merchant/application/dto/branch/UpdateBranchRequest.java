package com.walking.merchant.application.dto.branch;

import com.walking.merchant.domain.entity.branch.OpeningHours;
import jakarta.validation.constraints.*;

import java.util.List;

public record UpdateBranchRequest(
        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Double longitude,

        List<OpeningHours> openingHours
) {
}
