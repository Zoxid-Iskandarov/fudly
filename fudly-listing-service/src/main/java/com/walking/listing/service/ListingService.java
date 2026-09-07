package com.walking.listing.service;

import com.walking.listing.client.MerchantServiceClient;
import com.walking.listing.domain.dto.listing.*;
import com.walking.listing.domain.entity.listing.Listing;
import com.walking.listing.domain.entity.listing.ListingStatus;
import com.walking.listing.domain.entity.outbox.EventType;
import com.walking.listing.domain.entity.outbox.OutboxEvent;
import com.walking.listing.domain.entity.outbox.OutboxStatus;
import com.walking.listing.domain.exception.InvalidListingOperationException;
import com.walking.listing.domain.exception.ListingNotEditableException;
import com.walking.listing.domain.exception.ResourceNotFoundException;
import com.walking.listing.repository.ListingRepository;
import com.walking.listing.repository.OutboxEventRepository;
import com.walking.listing.repository.specification.ListingSpecification;
import com.walking.listing.mapper.listing.CreateListingRequestMapper;
import com.walking.listing.mapper.listing.ListingResponseMapper;
import com.walking.listing.mapper.listing.UpdateActiveListingRequestMapper;
import com.walking.listing.mapper.listing.UpdateDraftListingRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ListingService {
    private final ListingRepository listingRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final MerchantServiceClient merchantServiceClient;
    private final ObjectMapper objectMapper;

    private final ListingResponseMapper listingResponseMapper;
    private final CreateListingRequestMapper createListingRequestMapper;
    private final UpdateDraftListingRequestMapper updateDraftListingRequestMapper;
    private final UpdateActiveListingRequestMapper updateActiveListingRequestMapper;

    public Page<ListingResponse> searchListings(ListingSearchRequest request, Pageable pageable, Jwt jwt) {
        ListingStatus effectiveStatus = request.status();

        if (effectiveStatus == null) {
            effectiveStatus = ListingStatus.ACTIVE;
        } else if (effectiveStatus != ListingStatus.ACTIVE) {
            if (jwt == null || request.branchId() == null) {
                throw new AccessDeniedException("Authentication and branchId required to view non-active listings");
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
            throw new ListingNotEditableException("Full edit is only allowed while listing is in DRAFT");
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
            throw new ListingNotEditableException("This operation is only allowed while listing is ACTIVE");
        }

        updateActiveListingRequestMapper.toEntity(request, listing);
        return listingResponseMapper.toDto(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponse publishListing(UUID listingId, Jwt jwt) {
        Listing listing = findListingOrThrow(listingId);
        checkBranchAccess(listing.getBranchId(), jwt);

        if (listing.getStatus() != ListingStatus.DRAFT) {
            throw new InvalidListingOperationException("Only DRAFT listings can be published");
        }
        if (!merchantServiceClient.canPublishListing(listing.getBranchId())) {
            throw new InvalidListingOperationException("Cannot publish listing because branch or merchant is not active");
        }

        listing.setStatus(ListingStatus.ACTIVE);
        Listing publishedListing = listingRepository.save(listing);

        createOutboxEvent(publishedListing.getId(), EventType.LISTING_PUBLISHED, new ListingPublishedPayload(
                publishedListing.getId(),
                publishedListing.getBranchId(),
                publishedListing.getQuantity(),
                publishedListing.getDiscountedPrice(),
                publishedListing.getExpirationTime()));

        return listingResponseMapper.toDto(publishedListing);
    }

    @Transactional
    public ListingResponse cancelListing(UUID listingId, Jwt jwt) {
        Listing listing = findListingOrThrow(listingId);
        checkBranchAccess(listing.getBranchId(), jwt);

        if (listing.getStatus() != ListingStatus.DRAFT && listing.getStatus() != ListingStatus.ACTIVE) {
            throw new InvalidListingOperationException("Only DRAFT or ACTIVE listings can be cancelled");
        }

        boolean wasActive = listing.getStatus() == ListingStatus.ACTIVE;

        listing.setStatus(ListingStatus.CANCELLED);
        Listing cancelledListing = listingRepository.save(listing);

        if (wasActive) {
            createOutboxEvent(cancelledListing.getId(), EventType.LISTING_CANCELLED,
                    new ListingCancelledPayload(cancelledListing.getId(), cancelledListing.getBranchId()));
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

    private void createOutboxEvent(UUID aggregateId, EventType eventType, Object payload) {
        outboxEventRepository.save(OutboxEvent.builder()
                .aggregateType("Listing")
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(objectMapper.writeValueAsString(payload))
                .status(OutboxStatus.PENDING)
                .build());
    }
}
