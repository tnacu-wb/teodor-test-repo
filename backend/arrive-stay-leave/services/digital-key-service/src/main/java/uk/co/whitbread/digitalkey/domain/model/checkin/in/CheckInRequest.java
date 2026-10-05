package uk.co.whitbread.digitalkey.domain.model.checkin.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInRequest {

  private String reservationNumber;
  private String hotelId;
  private String roomId;

}
