package uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.preferences.out.HotelPreferencesResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.preferences.model.out.HotelPreferencesResponseDto;

@Mapper(componentModel = "spring")
public interface HotelPreferencesResponseMapper {

  HotelPreferencesResponseDto toResponseDto(HotelPreferencesResponse hotelPreferencesResponse);
}
