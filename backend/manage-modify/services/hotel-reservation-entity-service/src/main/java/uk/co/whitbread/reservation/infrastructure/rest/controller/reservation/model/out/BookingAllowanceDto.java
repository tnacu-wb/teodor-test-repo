package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingAllowanceDto {

  private String allowance;
  private BigDecimal budget;

}