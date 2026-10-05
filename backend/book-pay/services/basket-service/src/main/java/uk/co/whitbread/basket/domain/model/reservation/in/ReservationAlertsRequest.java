package uk.co.whitbread.basket.domain.model.reservation.in;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAlertsRequest {
  private String hotelId;
  private Set<String> reservationIds;
  private List<Alert> alerts;
}
