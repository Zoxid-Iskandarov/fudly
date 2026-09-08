package com.walking.merchant.application.dto.branch;

import jakarta.validation.constraints.*;

public record GeoSearchRequest(

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Double longitude,

        @NotNull(message = "Radius is required")
        @Min(value = 1, message = "Radius must be at least 1 km")
        @Max(value = 100, message = "Radius must be at most 100 km")
        Integer radius,

        Boolean onlyOpen
) {

    public Boolean isOnlyOpen() {
        return Boolean.TRUE.equals(onlyOpen);
    }
}
