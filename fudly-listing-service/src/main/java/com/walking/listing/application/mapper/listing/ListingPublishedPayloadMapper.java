package com.walking.listing.application.mapper.listing;

import com.walking.listing.application.dto.listing.ListingPublishedPayload;
import com.walking.listing.domain.entity.listing.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ListingPublishedPayloadMapper {

    @Mapping(target = "listingId", source = "id")
    ListingPublishedPayload toPayload(Listing listing);
}
