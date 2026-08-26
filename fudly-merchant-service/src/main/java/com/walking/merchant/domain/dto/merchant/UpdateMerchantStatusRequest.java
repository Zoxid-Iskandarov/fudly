package com.walking.merchant.domain.dto.merchant;

import com.walking.merchant.domain.entity.merchant.MerchantStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateMerchantStatusRequest(
        @NotNull(message = "Status is required")
        MerchantStatus status
) {
}
