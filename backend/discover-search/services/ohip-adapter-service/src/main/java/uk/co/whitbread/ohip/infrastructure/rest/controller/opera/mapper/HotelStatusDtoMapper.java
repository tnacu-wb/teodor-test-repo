package uk.co.whitbread.ohip.infrastructure.rest.controller.opera.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.infrastructure.rest.controller.opera.model.out.HotelStatusDto;

@Mapper(componentModel = "spring")
public interface HotelStatusDtoMapper {

  HotelStatusDto toDto(HotelStatus hotelInfo);

}