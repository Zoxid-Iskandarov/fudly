package com.walking.listing.application.mapper.listing;

import com.walking.listing.application.dto.listing.CreateListingRequest;
import com.walking.listing.domain.entity.listing.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateListingRequestMapper {

    Listing toEntity(CreateListingRequest createListingRequest);
}
