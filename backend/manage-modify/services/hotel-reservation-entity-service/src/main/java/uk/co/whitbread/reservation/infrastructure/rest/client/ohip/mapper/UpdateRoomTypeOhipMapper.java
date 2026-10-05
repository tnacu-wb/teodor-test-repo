package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomTypeChangeRequestDto;
import uk.co.whitbread.reservation.domain.model.in.UpdateRequest;

@Mapper(componentModel = "spring")
public interface UpdateRoomTypeOhipMapper {

  RoomTypeChangeRequestDto toDto(UpdateRequest updateRoomTypeRequest);

}
