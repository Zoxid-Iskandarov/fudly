package com.walking.merchant.domain.dto.error;

import java.time.OffsetDateTime;

public record ErrorResponse(
        Integer status,
        String error,
        String message,
        OffsetDateTime timestamp
) {
}
