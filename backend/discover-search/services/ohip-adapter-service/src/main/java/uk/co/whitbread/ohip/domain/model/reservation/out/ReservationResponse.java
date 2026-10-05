package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationResponse {

  private List<ReservationCreationResponse> reservations;
  private BigDecimal totalCost;
  private String hotelId;
  private String currencyCode;


}
