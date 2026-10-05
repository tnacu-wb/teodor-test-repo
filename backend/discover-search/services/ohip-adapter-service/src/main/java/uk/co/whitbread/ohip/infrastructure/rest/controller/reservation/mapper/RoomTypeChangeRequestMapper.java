package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomTypeChangeRequestDto;

@Mapper(componentModel = "spring")
public interface RoomTypeChangeRequestMapper {

  RatePlanRoomTypeChangeRequest toModel(RoomTypeChangeRequestDto ratePlanChangeRequestDto);
}
