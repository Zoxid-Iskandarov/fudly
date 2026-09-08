package com.walking.merchant.application.mapper.merchant;

import com.walking.merchant.application.dto.merchant.MerchantRequest;
import com.walking.merchant.domain.entity.merchant.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantRequestMapper {

    Merchant toEntity(MerchantRequest merchantRequest);

    void toEntity(MerchantRequest merchantRequest, @MappingTarget Merchant merchant);
}
