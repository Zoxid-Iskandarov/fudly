package com.walking.listing.application.service;

import com.walking.listing.application.dto.listing.*;
import com.walking.listing.client.MerchantServiceClient;
import com.walking.listing.domain.entity.listing.Listing;
import com.walking.listing.domain.entity.listing.ListingStatus;
import com.walking.listing.domain.entity.outbox.EventType;
import com.walking.listing.domain.exception.InvalidListingOperationException;
import com.walking.listing.domain.exception.ListingNotEditableException;
import com.walking.listing.domain.exception.ResourceNotFoundException;
import com.walking.listing.dto.listing.*;
import com.walking.listing.domain.repository.ListingRepository;
import com.walking.listing.domain.repository.specification.ListingSpecification;
import com.walking.listing.application.mapper.listing.CreateListingRequestMapper;
import com.walking.listing.application.mapper.listing.ListingCancelledPayloadMapper;
import com.walking.listing.application.mapper.listing.ListingPublishedPayloadMapper;
import com.walking.listing.application.mapper.listing.ListingResponseMapper;
import com.walking.listing.application.mapper.listing.UpdateActiveListingRequestMapper;
import com.walking.listing.application.mapper.listing.UpdateDraftListingRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;
    private final OutboxEventService outboxEventService;
    private final MerchantServiceClient merchantServiceClient;

    private final ListingResponseMapper listingResponseMapper;
    private final CreateListingRequestMapper createListingRequestMapper;
    private final UpdateDraftListingRequestMapper updateDraftListingRequestMapper;
    private final UpdateActiveListingRequestMapper updateActiveListingRequestMapper;
    private final ListingPublishedPayloadMapper listingPublishedPayloadMapper;
    private final ListingCancelledPayloadMapper listingCancelledPayloadMapper;

    public Page<ListingResponse> searchListings(ListingSearchRequest request, Pageable pageable, Jwt jwt) {
        ListingStatus effectiveStatus = request.status();

        if (effectiveStatus == null) {
            effectiveStatus = ListingStatus.ACTIVE;
        } else if (effectiveStatus != ListingStatus.ACTIVE) {
            if (jwt == null || request.branchId() == null) {
                throw new AccessDeniedException("Authentication and branchId are required to view non-active listings");
            }
            checkBranchAccess(request.branchId(), jwt);
        }

        Specification<Listing> spec = Specification.where(ListingSpecification.hasStatus(effectiveStatus))
                .and(ListingSpecification.hasBranchId(request.branchId()));

        return listingRepository.findAll(spec, pageable)
                .map(listingResponseMapper::toDto);
    }

    public ListingResponse getListingById(UUID listingId) {
        return listingResponseMapper.toDto(findListingOrThrow(listingId));
    }

    @Transactional
    public ListingResponse createListing(CreateListingRequest request, Jwt jwt) {
        validateDiscount(request.originalPrice(), request.discountedPrice());
        checkBranchAccess(request.branchId(), jwt);

        Listing listing = createListingRequestMapper.toEntity(request);
        listing.setStatus(ListingStatus.DRAFT);

        return listingResponseMapper.toDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponse updateDraftListing(UUID listingId, UpdateDraftListingRequest request, Jwt jwt) {
        Listing listing = findListingOrThrow(listingId);
        checkBranchAccess(listing.getBranchId(), jwt);

        if (listing.getStatus() != ListingStatus.DRAFT) {
            throw new ListingNotEditableException("Listing can only be fully edited while in DRAFT status");
        }

        validateDiscount(request.originalPrice(), request.discountedPrice());

        updateDraftListingRequestMapper.toEntity(request, listing);
        return listingResponseMapper.toDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponse updateActiveListing(UUID listingId, UpdateActiveListingRequest request, Jwt jwt) {
        Listing listing = findListingOrThrow(listingId);
        checkBranchAccess(listing.getBranchId(), jwt);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new ListingNotEditableException("This operation is only allowed when listing is ACTIVE");
        }

        updateActiveListingRequestMapper.toEntity(request, listing);
        return listingResponseMapper.toDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponse publishListing(UUID listingId, Jwt jwt) {
        Listing listing = findListingOrThrow(listingId);
        checkBranchAccess(listing.getBranchId(), jwt);

        if (listing.getStatus() != ListingStatus.DRAFT) {
            throw new InvalidListingOperationException("Only listings in DRAFT status can be published");
        }
        if (!merchantServiceClient.canPublishListing(listing.getBranchId())) {
            throw new InvalidListingOperationException("Cannot publish listing: branch or merchant is not active");
        }

        listing.setStatus(ListingStatus.ACTIVE);
        Listing publishedListing = listingRepository.save(listing);

        outboxEventService.createOutboxEvent(publishedListing.getId(), EventType.LISTING_PUBLISHED,
                listingPublishedPayloadMapper.toPayload(publishedListing));

        return listingResponseMapper.toDto(publishedListing);
    }

    @Transactional
    public ListingResponse cancelListing(UUID listingId, Jwt jwt) {
        Listing listing = findListingOrThrow(listingId);
        checkBranchAccess(listing.getBranchId(), jwt);

        if (listing.getStatus() != ListingStatus.DRAFT && listing.getStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException("Only listings in DRAFT or ACTIVE status can be cancelled");
        }

        boolean wasActive = listing.getStatus() == ListingStatus.ACTIVE;

        listing.setStatus(ListingStatus.CANCELLED);
        Listing cancelledListing = listingRepository.save(listing);

        if (wasActive) {
            outboxEventService.createOutboxEvent(cancelledListing.getId(), EventType.LISTING_CANCELLED,
                    listingCancelledPayloadMapper.toPayload(cancelledListing));
        }

        return listingResponseMapper.toDto(cancelledListing);
    }

    private Listing findListingOrThrow(UUID listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing with id %s not found".formatted(listingId)));
    }

    private void validateDiscount(BigDecimal originalPrice, BigDecimal discountedPrice) {
        if (discountedPrice.compareTo(originalPrice) >= 0) {
            throw new InvalidListingOperationException("Discounted price must be less than original price");
        }
    }

    private void checkBranchAccess(UUID branchId, Jwt jwt) {
        UUID userId = UUID.fromString(Objects.requireNonNull(jwt.getSubject()));
        if (!merchantServiceClient.hasAccess(branchId, userId)) {
            throw new AccessDeniedException("User %s does not have access to branch %s".formatted(userId, branchId));
        }
    }
}
