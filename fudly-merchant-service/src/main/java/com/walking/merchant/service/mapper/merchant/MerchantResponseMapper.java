package com.walking.merchant.service.mapper.merchant;

import com.walking.merchant.domain.dto.merchant.MerchantResponse;
import com.walking.merchant.domain.entity.merchant.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantResponseMapper {

    MerchantResponse toDto(Merchant merchant);
}
