package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OhipReservationResponse {

  private List<OhipReservationCreationResponse> reservations;
  private BigDecimal totalCost;
  private String hotelId;
  private String currencyCode;

}
