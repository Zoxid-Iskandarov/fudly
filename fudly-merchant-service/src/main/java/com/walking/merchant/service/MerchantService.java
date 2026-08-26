package com.walking.merchant.service;

import com.walking.merchant.domain.dto.merchant.MerchantRequest;
import com.walking.merchant.domain.dto.merchant.MerchantResponse;
import com.walking.merchant.domain.entity.merchant.Merchant;
import com.walking.merchant.domain.entity.merchant.MerchantStatus;
import com.walking.merchant.domain.exception.InvalidStatusTransitionException;
import com.walking.merchant.domain.exception.ResourceNotFoundException;
import com.walking.merchant.repository.BranchRepository;
import com.walking.merchant.repository.MerchantRepository;
import com.walking.merchant.service.mapper.merchant.MerchantRequestMapper;
import com.walking.merchant.service.mapper.merchant.MerchantResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;
    private final BranchRepository branchRepository;

    private final MerchantRequestMapper merchantRequestMapper;
    private final MerchantResponseMapper merchantResponseMapper;

    public MerchantResponse getMerchantById(UUID merchantId) {
        return merchantRepository.findById(merchantId)
                .map(merchantResponseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id %s not found".formatted(merchantId)));
    }

    public List<MerchantResponse> getMerchantsByOwnerId(Jwt jwt) {
        UUID ownerId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));

        return merchantRepository.findByOwnerId(ownerId).stream()
                .map(merchantResponseMapper::toDto)
                .toList();
    }

    @Transactional
    public MerchantResponse createMerchant(MerchantRequest merchantRequest, Jwt jwt) {
        Merchant merchant = merchantRequestMapper.toEntity(merchantRequest);
        merchant.setStatus(MerchantStatus.PENDING_VERIFICATION);
        merchant.setOwnerId(UUID.fromString(Objects.requireNonNull(jwt.getSubject())));

        Merchant savedMerchant = merchantRepository.save(merchant);

        return merchantResponseMapper.toDto(savedMerchant);
    }

    @Transactional
    public MerchantResponse updateMerchant(UUID merchantId, MerchantRequest merchantRequest, Jwt jwt) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id %s not found".formatted(merchantId)));

        UUID ownerId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        if (!merchant.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner of the merchant");
        }

        merchantRequestMapper.toEntity(merchantRequest, merchant);
        Merchant updatedMerchant = merchantRepository.save(merchant);

        return merchantResponseMapper.toDto(updatedMerchant);
    }

    @Transactional
    public MerchantResponse closeMerchant(UUID merchantId, Jwt jwt) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id %s not found".formatted(merchantId)));

        UUID ownerId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        if (!merchant.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Not the owner of the merchant");
        }

        if (merchant.getStatus() == MerchantStatus.CLOSED) {
            throw new InvalidStatusTransitionException("Merchant is already closed");
        }

        merchant.setStatus(MerchantStatus.CLOSED);
        Merchant updatedMerchant = merchantRepository.save(merchant);

        branchRepository.closeAllBranchesByMerchantId(merchantId);

        return merchantResponseMapper.toDto(updatedMerchant);
    }

    @Transactional
    public MerchantResponse updateMerchantStatus(UUID merchantId, MerchantStatus newStatus) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant with id %s not found".formatted(merchantId)));

        MerchantStatus current = merchant.getStatus();
        boolean allowed = switch (current) {
            case PENDING_VERIFICATION -> newStatus == MerchantStatus.ACTIVE || newStatus == MerchantStatus.SUSPENDED;
            case ACTIVE -> newStatus == MerchantStatus.SUSPENDED;
            case SUSPENDED -> newStatus == MerchantStatus.ACTIVE;
            case CLOSED -> false;
        };

        if (!allowed) {
            throw new InvalidStatusTransitionException("Cannot transition merchant from %s to %s"
                    .formatted(current, newStatus));
        }

        merchant.setStatus(newStatus);
        Merchant updatedMerchant = merchantRepository.save(merchant);

        return merchantResponseMapper.toDto(updatedMerchant);
    }
}
