package com.walking.merchant.service;

import com.walking.merchant.domain.entity.branch.Branch;
import com.walking.merchant.domain.entity.staff.BranchStaff;
import com.walking.merchant.domain.entity.staff.BranchStaffId;
import com.walking.merchant.domain.exception.ResourceNotFoundException;
import com.walking.merchant.repository.BranchRepository;
import com.walking.merchant.repository.BranchStaffRepository;
import com.walking.merchant.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BranchStaffService {
    private final BranchStaffRepository branchStaffRepository;
    private final BranchRepository branchRepository;

    public boolean hasAccessToBranch(UUID branchId, UUID userId) {
        Branch branch = branchRepository.findByIdWithMerchant(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));

        boolean isOwner = branch.getMerchant().getOwnerId().equals(userId);
        boolean isStaff = branchStaffRepository.existsById(new BranchStaffId(branchId, userId));

        return isOwner || isStaff;
    }

    @Transactional
    public void assignStaffToBranch(UUID branchId, UUID userId, Jwt jwt) {
        Branch branch = branchRepository.findByIdWithMerchant(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));

        SecurityUtils.validateBranchOwnership(branch, jwt);

        branchStaffRepository.save(new BranchStaff(branchId, userId));
    }

    @Transactional
    public void removeStaffFromBranch(UUID branchId, UUID userId, Jwt jwt) {
        Branch branch = branchRepository.findByIdWithMerchant(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));

        SecurityUtils.validateBranchOwnership(branch, jwt);

        branchStaffRepository.deleteById(new BranchStaffId(branchId, userId));
    }
}
