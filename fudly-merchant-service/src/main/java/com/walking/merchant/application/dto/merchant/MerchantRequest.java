package com.walking.merchant.application.dto.merchant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MerchantRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description,

        @NotBlank(message = "Phone is required")
        @Size(max = 100, message = "Phone must be at most 100 characters")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a well-formed email address")
        @Size(max = 100, message = "Email must be at most 100 characters")
        String email
) {}
