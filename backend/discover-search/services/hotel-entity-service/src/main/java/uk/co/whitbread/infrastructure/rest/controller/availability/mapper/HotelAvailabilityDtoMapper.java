package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityResponseDto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilityDtoMapper {

  HotelAvailabilityResponseDto toDto(HotelAvailability hotelAvailability);

}
