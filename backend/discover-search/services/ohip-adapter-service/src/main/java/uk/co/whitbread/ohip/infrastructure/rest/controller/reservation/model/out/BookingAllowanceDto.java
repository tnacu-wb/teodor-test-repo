package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class BookingAllowanceDto {

  private String allowance;
  private BigDecimal budget;

}
