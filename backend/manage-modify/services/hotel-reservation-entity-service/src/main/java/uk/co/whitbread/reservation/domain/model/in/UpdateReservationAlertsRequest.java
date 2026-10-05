package uk.co.whitbread.reservation.domain.model.in;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationAlertsRequest {

  private Set<String> reservationIds;
  private String hotelId;
  private List<Alert> alerts;
}
