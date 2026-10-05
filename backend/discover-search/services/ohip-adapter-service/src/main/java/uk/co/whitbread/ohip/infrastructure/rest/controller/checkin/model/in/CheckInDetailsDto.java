package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDetailsDto {

  private String hotelId;
  private String reservationNumber;
  private String roomId;

}
