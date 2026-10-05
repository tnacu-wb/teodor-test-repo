package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.HouseKeepingResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface HouseKeepingRoomStatusResponseMapper {

  @Named("toHouseKeepingResponseDto")
  default HouseKeepingResponseDto toHouseKeepingResponseDto(
      HouseKeepingRoomStatusResponse houseKeepingRoomStatusResponse, String roomId) {
    HouseKeepingResponseDto houseKeepingResponseDto = new HouseKeepingResponseDto();
    final var housekeepingRooms = houseKeepingRoomStatusResponse.getHousekeepingRoomInfo()
        .getHousekeepingRooms();

    houseKeepingResponseDto.setHotelId(housekeepingRooms.getHotelId());
    if (null != housekeepingRooms.getRoom()) {
      houseKeepingResponseDto.setStatus(
          housekeepingRooms.getRoom().get(0).getHousekeeping().getHousekeepingRoomStatus()
              .getHousekeepingRoomStatusText());
      houseKeepingResponseDto.setRoomId(housekeepingRooms.getRoom().get(0).getRoomId());
    } else {
      houseKeepingResponseDto.setRoomId(roomId);
      houseKeepingResponseDto.setStatus("RoomId Not Available");
    }

    return houseKeepingResponseDto;

  }


}
