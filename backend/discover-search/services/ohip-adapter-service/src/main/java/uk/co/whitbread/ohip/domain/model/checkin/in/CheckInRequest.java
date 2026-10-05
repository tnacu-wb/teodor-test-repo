package uk.co.whitbread.ohip.domain.model.checkin.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {

  private Reservation reservation;
  private List<String> fetchReservationInstruction;
  private boolean includeNotifications = true;

}
