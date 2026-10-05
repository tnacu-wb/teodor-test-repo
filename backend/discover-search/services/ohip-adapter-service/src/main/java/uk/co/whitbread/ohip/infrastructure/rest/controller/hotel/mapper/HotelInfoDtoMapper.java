package uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.hotel.out.HotelInfo;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.HotelInfoDto;

@Mapper(componentModel = "spring")
public interface HotelInfoDtoMapper {

  HotelInfoDto toDto(HotelInfo hotelInfo);

}
