package com.walking.merchant.application.service;

import com.walking.merchant.application.dto.branch.*;
import com.walking.merchant.domain.entity.branch.Branch;
import com.walking.merchant.domain.entity.branch.BranchStatus;
import com.walking.merchant.domain.entity.merchant.Merchant;
import com.walking.merchant.domain.entity.merchant.MerchantStatus;
import com.walking.merchant.domain.exception.InvalidStatusTransitionException;
import com.walking.merchant.domain.exception.MerchantNotActiveException;
import com.walking.merchant.domain.exception.ResourceNotFoundException;
import com.walking.merchant.domain.repository.BranchRepository;
import com.walking.merchant.domain.repository.MerchantRepository;
import com.walking.merchant.security.SecurityUtils;
import com.walking.merchant.application.mapper.branch.BranchResponseMapper;
import com.walking.merchant.application.mapper.branch.CreateBranchRequestMapper;
import com.walking.merchant.application.mapper.branch.UpdateBranchRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BranchService {
    private final BranchRepository branchRepository;
    private final MerchantRepository merchantRepository;

    private final BranchResponseMapper branchResponseMapper;
    private final CreateBranchRequestMapper createBranchRequestMapper;
    private final UpdateBranchRequestMapper updateBranchRequestMapper;

    public BranchResponse getBranchById(UUID branchId) {
        return branchRepository.findById(branchId)
                .map(branchResponseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));
    }

    public List<BranchNearResponse> findNearByBranches(GeoSearchRequest request) {
        return branchRepository.findNearByBranches(
                request.latitude(), request.longitude(), request.radius(), request.isOnlyOpen());
    }

    @Transactional
    public BranchResponse createBranch(CreateBranchRequest createBranchRequest, Jwt jwt) {
        Merchant merchant = merchantRepository.findById(createBranchRequest.merchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id %s not found"
                        .formatted(createBranchRequest.merchantId())));

        SecurityUtils.validateMerchantOwnership(merchant, jwt);

        if (merchant.getStatus() != MerchantStatus.ACTIVE) {
            throw new MerchantNotActiveException(
                    "Merchant must be ACTIVE to create branches, current status: %s".formatted(merchant.getStatus()));
        }

        Branch branch = createBranchRequestMapper.toEntity(createBranchRequest);
        branch.setMerchant(merchant);
        branch.setStatus(BranchStatus.OPEN);

        Branch savedBranch = branchRepository.save(branch);

        return branchResponseMapper.toDto(savedBranch);
    }

    @Transactional
    public BranchResponse updateBranch(UUID branchId, UpdateBranchRequest updateBranchRequest, Jwt jwt) {
        Branch branch = branchRepository.findByIdWithMerchant(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));

        SecurityUtils.validateBranchOwnership(branch, jwt);

        updateBranchRequestMapper.toEntity(updateBranchRequest, branch);
        Branch updatedBranch = branchRepository.save(branch);

        return branchResponseMapper.toDto(updatedBranch);
    }

    @Transactional
    public BranchResponse updateBranchStatus(UUID branchId, BranchStatus newStatus, Jwt jwt) {
        Branch branch = branchRepository.findByIdWithMerchant(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));

        SecurityUtils.validateBranchOwnership(branch, jwt);

        BranchStatus current = branch.getStatus();
        boolean allowed = switch (current) {
            case OPEN -> newStatus == BranchStatus.TEMPORARILY_CLOSED || newStatus == BranchStatus.CLOSED;
            case TEMPORARILY_CLOSED -> newStatus == BranchStatus.OPEN || newStatus == BranchStatus.CLOSED;
            case CLOSED -> false;
        };

        if (!allowed) {
            throw new InvalidStatusTransitionException("Cannot transition branch from %s to %s"
                    .formatted(current, newStatus));
        }

        if (newStatus == BranchStatus.OPEN && branch.getMerchant().getStatus() != MerchantStatus.ACTIVE) {
            throw new MerchantNotActiveException(
                    "Cannot reopen branch while merchant is not ACTIVE, current merchant status: %s"
                            .formatted(branch.getMerchant().getStatus()));
        }

        branch.setStatus(newStatus);
        Branch updatedBranch = branchRepository.save(branch);

        return branchResponseMapper.toDto(updatedBranch);
    }

    public boolean canPublishListing(UUID branchId) {
        Branch branch = branchRepository.findByIdWithMerchant(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch with id %s not found".formatted(branchId)));

        return branch.getStatus() == BranchStatus.OPEN && branch.getMerchant().getStatus() == MerchantStatus.ACTIVE;
    }
}
