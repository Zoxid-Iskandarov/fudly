package com.walking.merchant.security;

import com.walking.merchant.domain.entity.branch.Branch;
import com.walking.merchant.domain.entity.merchant.Merchant;
import lombok.experimental.UtilityClass;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class SecurityUtils {

    public UUID extractOwnerId(Jwt jwt) {
        return UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
    }

    public void validateMerchantOwnership(Merchant merchant, Jwt jwt) {
        UUID ownerId = extractOwnerId(jwt);
        if (!merchant.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner of the merchant");
        }
    }

    public void validateBranchOwnership(Branch branch, Jwt jwt) {
        UUID ownerId = extractOwnerId(jwt);
        if (!branch.getMerchant().getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner of this branch's merchant");
        }
    }
}
