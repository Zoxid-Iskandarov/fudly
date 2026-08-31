package com.walking.listing.service.mapper;

import com.walking.listing.domain.dto.listing.UpdateActiveListingRequest;
import com.walking.listing.domain.entity.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateActiveListingRequestMapper {

    void toEntity(UpdateActiveListingRequest updateActiveListingRequest, @MappingTarget Listing listing);
}
