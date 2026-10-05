package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.in.HotelAvailabilityRequestDto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilityRequestDtoMapper {

  HotelAvailabilityRequest toModel(HotelAvailabilityRequestDto hotelAvailabilityRequestDto);
}
