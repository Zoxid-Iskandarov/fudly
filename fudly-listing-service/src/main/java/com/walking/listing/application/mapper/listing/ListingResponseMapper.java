package com.walking.listing.application.mapper.listing;

import com.walking.listing.application.dto.listing.ListingResponse;
import com.walking.listing.domain.entity.listing.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ListingResponseMapper {

    ListingResponse toDto(Listing listing);
}
