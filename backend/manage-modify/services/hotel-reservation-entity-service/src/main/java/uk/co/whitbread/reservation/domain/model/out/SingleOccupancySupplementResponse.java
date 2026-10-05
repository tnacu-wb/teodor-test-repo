package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleOccupancySupplementResponse {

  private String hotelId;
  private BigDecimal pricing;
}
