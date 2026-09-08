package com.walking.merchant.application.mapper.branch;

import com.walking.merchant.application.dto.branch.UpdateBranchRequest;
import com.walking.merchant.domain.entity.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UpdateBranchRequestMapper {

    void toEntity(UpdateBranchRequest updateBranchRequest, @MappingTarget Branch branch);
}
