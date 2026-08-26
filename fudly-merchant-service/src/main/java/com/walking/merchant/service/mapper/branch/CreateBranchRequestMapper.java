package com.walking.merchant.service.mapper.branch;

import com.walking.merchant.domain.dto.branch.CreateBranchRequest;
import com.walking.merchant.domain.entity.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateBranchRequestMapper {

    Branch toEntity(CreateBranchRequest createBranchRequest);
}
