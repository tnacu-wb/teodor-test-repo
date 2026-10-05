package uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper;


import java.util.Arrays;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomType;
import uk.co.whitbread.content.domain.model.roomtype.out.RoomTypeInformation;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeDto;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.in.RoomTypeInformationDto;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

  RoomType toDomainModel(RoomTypeDto roomTypeDto);

  @Mapping(target = "roomTypeCode", source = "roomTypeCode", qualifiedByName = "toListRoomTypeCodeModel")
  RoomTypeInformation toRoomTypeInfoDomainModel(RoomTypeInformationDto roomTypeInformationDto);

  @Named("toListRoomTypeCodeModel")
  default List<String> toListRoomTypeCodeModel(String roomTypeCode) {
    return Arrays.stream(roomTypeCode.split(",")).map(String::strip).toList();
  }
}
