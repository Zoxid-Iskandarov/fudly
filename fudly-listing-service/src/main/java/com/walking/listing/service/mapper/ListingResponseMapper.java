package com.walking.listing.service.mapper;

import com.walking.listing.domain.dto.listing.ListingResponse;
import com.walking.listing.domain.entity.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ListingResponseMapper {

    ListingResponse toDto(Listing listing);
}
