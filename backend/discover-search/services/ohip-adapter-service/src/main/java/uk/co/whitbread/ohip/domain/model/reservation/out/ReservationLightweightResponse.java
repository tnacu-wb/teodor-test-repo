package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ReservationLightweightResponse {
  private List<LightweightReservationById> reservationByIdList;
}
