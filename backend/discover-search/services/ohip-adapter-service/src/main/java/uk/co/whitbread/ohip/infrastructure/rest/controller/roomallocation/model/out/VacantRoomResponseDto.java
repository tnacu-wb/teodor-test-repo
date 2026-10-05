package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VacantRoomResponseDto {

  private HotelRoomsDetailsDto hotelRoomsDetails;

}
