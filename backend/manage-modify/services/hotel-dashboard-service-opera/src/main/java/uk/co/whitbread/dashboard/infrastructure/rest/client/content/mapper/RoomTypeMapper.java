package uk.co.whitbread.dashboard.infrastructure.rest.client.content.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.domain.model.in.RoomTypeInformation;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.model.RoomTypeDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.model.RoomTypeInformationDto;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {

  RoomType toModel(RoomTypeDto roomTypeDto);

  RoomTypeInformation toModel(RoomTypeInformationDto roomTypeInformationDto);
}