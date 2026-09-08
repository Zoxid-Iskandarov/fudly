package com.walking.merchant.application.dto.branch;

import com.walking.merchant.domain.entity.branch.BranchStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateBranchStatusRequest(
        @NotNull(message = "Status is required")
        BranchStatus status
) {
}
