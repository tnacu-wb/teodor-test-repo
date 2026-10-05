package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CopyReservationsResponse {

  private List<CopyReservationResponse> reservations;
  private Map<String, String> linkBetweenReservations;
}
