package uk.co.whitbread.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.domain.model.hotel.out.HotelPreferencesResponse;
import uk.co.whitbread.infrastructure.rest.controller.hotel.model.out.HotelInfoDto;
import uk.co.whitbread.infrastructure.rest.controller.hotel.model.out.HotelPreferencesResponseDto;

@Mapper(componentModel = "spring")
public interface HotelInfoDtoMapper {

  HotelInfoDto toDto(HotelInfo hotelInfo);

  HotelPreferencesResponseDto toDto(HotelPreferencesResponse hotelPreferencesResponse);

}
