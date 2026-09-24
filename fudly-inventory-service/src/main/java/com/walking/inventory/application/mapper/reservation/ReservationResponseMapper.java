package com.walking.inventory.application.mapper.reservation;

import com.walking.inventory.application.dto.reservation.ReservationResponse;
import com.walking.inventory.domain.entity.reservation.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationResponseMapper {

    @Mapping(target = "reservationId", source = "id")
    ReservationResponse toDto(Reservation reservation);
}
