package uk.co.whitbread.kiosk.domain.model.checkin.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KioskCheckInRequest {

  private String hotelId;
  private String reservationNumber;
  private String roomId;

}
