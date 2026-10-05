package uk.co.whitbread.ohip.domain.model.reservation.out;

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

  public static final ReservationAmounts buildResAmountWithZero() {
    return ReservationAmounts.builder()
        .gross(BigDecimal.ZERO)
        .net(BigDecimal.ZERO)
        .deposit(BigDecimal.ZERO)
        .totalCostOfStay(BigDecimal.ZERO)
        .outStandingCostOfStay(BigDecimal.ZERO)
        .build();
  }
}
