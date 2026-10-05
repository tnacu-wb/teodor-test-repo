package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookingAllowance {

  private String allowance;
  private BigDecimal budget;

}
