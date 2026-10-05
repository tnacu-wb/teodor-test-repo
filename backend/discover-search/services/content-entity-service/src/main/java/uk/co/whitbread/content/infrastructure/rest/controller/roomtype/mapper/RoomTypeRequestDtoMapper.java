package uk.co.whitbread.content.infrastructure.rest.controller.roomtype.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.roomtype.model.in.RoomTypeRequestDto;

@Mapper(componentModel = "spring")
public interface RoomTypeRequestDtoMapper {

  @Mapping(target = "brand", expression = "java(toLowerCaseBrandModel(roomTypeRequestDto))")
  RoomTypeRequest toDomainModel(RoomTypeRequestDto roomTypeRequestDto);

  default String toLowerCaseBrandModel(RoomTypeRequestDto roomTypeRequestDto) {
    return roomTypeRequestDto.getBrand().toLowerCase();
  }

}
