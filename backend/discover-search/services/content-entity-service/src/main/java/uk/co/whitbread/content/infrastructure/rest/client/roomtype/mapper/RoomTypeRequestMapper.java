package uk.co.whitbread.content.infrastructure.rest.client.roomtype.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.roomtype.in.RoomTypeRequest;
import uk.co.whitbread.content.infrastructure.rest.client.roomtype.model.out.RoomTypeRequestAemDto;

@Mapper(componentModel = "spring")
public interface RoomTypeRequestMapper {

  RoomTypeRequestAemDto toDto(RoomTypeRequest request);
}
