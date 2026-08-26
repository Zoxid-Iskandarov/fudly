package com.walking.merchant.domain.dto.merchant;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String description,
        String phone,
        String email,
        String status
) {
}
