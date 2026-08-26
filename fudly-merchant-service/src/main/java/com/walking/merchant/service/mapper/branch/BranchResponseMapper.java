package com.walking.merchant.service.mapper.branch;

import com.walking.merchant.domain.dto.branch.BranchResponse;
import com.walking.merchant.domain.entity.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BranchResponseMapper {

    @Mapping(target = "merchantId", source = "merchant.id")
    BranchResponse toDto(Branch branch);
}
