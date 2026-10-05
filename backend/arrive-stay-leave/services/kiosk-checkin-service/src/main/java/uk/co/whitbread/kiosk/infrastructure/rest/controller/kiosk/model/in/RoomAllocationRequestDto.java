package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomAllocationRequestDto {

  private String reservationId;
  private String hotelId;
  private String roomType;
  private String roomId;

}
