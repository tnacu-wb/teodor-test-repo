package uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out.RoomTypesInfoDto;

@Mapper(componentModel = "spring")
public interface RoomTypesInfoDtoMapper {

  RoomTypesInfoDto toDto(RoomTypesInfo roomTypesInfo);

}
