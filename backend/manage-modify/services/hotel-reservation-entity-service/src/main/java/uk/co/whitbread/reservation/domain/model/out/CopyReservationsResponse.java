package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyReservationsResponse {

  private List<CopyReservationResponse> reservations;
  private Map<String, String> linkBetweenReservations;
}
