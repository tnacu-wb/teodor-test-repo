package uk.co.whitbread.content.infrastructure.rest.controller.roomtype.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.out.RoomTypeDto;

@Mapper(componentModel = "spring")
public interface RoomTypeDtoMapper {

  RoomTypeDto toDto(RoomType roomType);

}
