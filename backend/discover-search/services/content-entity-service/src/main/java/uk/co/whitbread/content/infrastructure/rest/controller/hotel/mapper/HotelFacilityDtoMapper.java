package uk.co.whitbread.content.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.hotel.out.HotelFacility;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.HotelFacilityDto;

@Mapper(componentModel = "spring")
public interface HotelFacilityDtoMapper {

  HotelFacilityDto toDto(HotelFacility facility);
}
