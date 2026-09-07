package com.walking.listing.mapper.listing;

import com.walking.listing.domain.dto.listing.UpdateDraftListingRequest;
import com.walking.listing.domain.entity.listing.Listing;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateDraftListingRequestMapper {

    void toEntity(UpdateDraftListingRequest updateDraftListingRequest, @MappingTarget Listing listing);
}
