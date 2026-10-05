package uk.co.whitbread.infrastructure.rest.controller.srp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.out.HotelAvailabilitiesResponseDto;
import uk.co.whitbread.infrastructure.rest.controller.srp.model.out.HotelAvailabilityResponseDto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilitiesResponseDtoMapper {

  HotelAvailabilityResponseDto toDto(HotelAvailabilityResponse hotelAvailabilityResponse);

  @Mapping(source = "hotelAvailabilityList", target = "hotelAvailabilities")
  HotelAvailabilitiesResponseDto toDtos(
      HotelAvailabilitiesResponse hotelAvailabilitySearchResponses);
}
