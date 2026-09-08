package com.walking.listing.application.mapper.listing;

import com.walking.listing.application.dto.listing.UpdateActiveListingRequest;
import com.walking.listing.domain.entity.listing.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateActiveListingRequestMapper {

    void toEntity(UpdateActiveListingRequest updateActiveListingRequest, @MappingTarget Listing listing);
}
