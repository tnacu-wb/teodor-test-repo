package uk.co.whitbread.kiosk.domain.model.checkin.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAmounts {

  private String currencyCode;
  private BigDecimal gross;
  private BigDecimal net;
  private BigDecimal deposit;
  private BigDecimal totalCostOfStay;
  private BigDecimal outStandingCostOfStay;
  private BigDecimal discount;
}