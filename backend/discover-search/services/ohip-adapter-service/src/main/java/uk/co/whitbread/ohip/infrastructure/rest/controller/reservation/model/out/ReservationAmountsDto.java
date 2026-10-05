package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationAmountsDto {

  private String currencyCode;
  private BigDecimal gross;
  private BigDecimal net;
  private BigDecimal deposit;
  private BigDecimal totalCostOfStay;
  private BigDecimal outStandingCostOfStay;
  private BigDecimal discount;
}
