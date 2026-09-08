package com.walking.listing.presentation.controller;

import com.walking.listing.application.dto.common.PageResponse;
import com.walking.listing.application.dto.listing.*;
import com.walking.listing.application.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @GetMapping
    public PageResponse<ListingResponse> getListings(
            ListingSearchRequest listingSearchRequest,
            @PageableDefault(size = 20, sort = "created", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        Page<ListingResponse> page = listingService.searchListings(listingSearchRequest, pageable, jwt);
        return PageResponse.from(page);
    }

    @GetMapping("/{listingId}")
    public ListingResponse getListing(@PathVariable UUID listingId) {
        return listingService.getListingById(listingId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MERCHANT_OWNER', 'MERCHANT_STAFF')")
    public ResponseEntity<ListingResponse> createListing(
            @RequestBody @Validated CreateListingRequest createListingRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.createListing(createListingRequest, jwt));
    }

    @PutMapping("/{listingId}")
    @PreAuthorize("hasAnyRole('MERCHANT_OWNER', 'MERCHANT_STAFF')")
    public ListingResponse updateDraftListing(
            @PathVariable UUID listingId,
            @RequestBody @Validated UpdateDraftListingRequest updateDraftListingRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.updateDraftListing(listingId, updateDraftListingRequest, jwt);
    }

    @PatchMapping("/{listingId}")
    @PreAuthorize("hasAnyRole('MERCHANT_OWNER', 'MERCHANT_STAFF')")
    public ListingResponse updateActiveListing(
            @PathVariable UUID listingId,
            @RequestBody @Validated UpdateActiveListingRequest updateActiveListingRequest,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.updateActiveListing(listingId, updateActiveListingRequest, jwt);
    }

    @PatchMapping("/{listingId}/publish")
    @PreAuthorize("hasAnyRole('MERCHANT_OWNER', 'MERCHANT_STAFF')")
    public ListingResponse publishListing(
            @PathVariable UUID listingId,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.publishListing(listingId, jwt);
    }

    @PatchMapping("/{listingId}/cancel")
    @PreAuthorize("hasAnyRole('MERCHANT_OWNER', 'MERCHANT_STAFF')")
    public ListingResponse cancelListing(
            @PathVariable UUID listingId,
            @AuthenticationPrincipal Jwt jwt) {
        return listingService.cancelListing(listingId, jwt);
    }
}
