package uk.co.whitbread.payments.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateInfoSummary {
  private String reservationId;

  private BigDecimal guestPayAmount;  // Window 1 - Credit Card
  private BigDecimal routingAmount;   // Window 2 - VCC Card Amount
  private String currency;

  public boolean requiresGuestPayment() {
    return guestPayAmount != null && guestPayAmount.compareTo(BigDecimal.ZERO) > 0;
  }

  public boolean hasRoutingAmount() {
    return routingAmount != null && routingAmount.compareTo(BigDecimal.ZERO) > 0;
  }
}
