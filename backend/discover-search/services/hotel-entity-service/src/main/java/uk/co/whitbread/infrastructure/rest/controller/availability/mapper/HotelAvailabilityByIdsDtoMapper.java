package uk.co.whitbread.infrastructure.rest.controller.availability.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsDto;
import uk.co.whitbread.infrastructure.rest.controller.availability.model.out.HotelAvailabilityByIdsV2Dto;

@Mapper(componentModel = "spring")
public interface HotelAvailabilityByIdsDtoMapper {

  HotelAvailabilityByIdsDto toDto(HotelAvailabilityByIds hotelAvailability);

  HotelAvailabilityByIdsV2Dto toV2Dto(HotelAvailabilityByIdsV2 hotelAvailabilityByIds);
}
