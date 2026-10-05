package uk.co.whitbread.payments.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedAmounts {
  private BigDecimal totalGuestPay;
  private BigDecimal totalRouting;

  /**
   * Check if guest payment is required (guestPay > 0).
   *
   * @return true if totalGuestPay is greater than zero
   */
  public boolean requiresGuestPayment() {
    return totalGuestPay != null && totalGuestPay.compareTo(BigDecimal.ZERO) > 0;
  }

  /**
   * Check if routing amount exists (routing > 0).
   *
   * @return true if totalRouting is greater than zero
   */
  public boolean hasRoutingAmount() {
    return totalRouting != null && totalRouting.compareTo(BigDecimal.ZERO) > 0;
  }

  /**
   * Get total guest pay amount, defaulting to ZERO if null.
   *
   * @return totalGuestPay or ZERO if null
   */
  public BigDecimal getTotalGuestPay() {
    return totalGuestPay != null ? totalGuestPay : BigDecimal.ZERO;
  }

  /**
   * Get total routing amount, defaulting to ZERO if null.
   *
   * @return totalRouting or ZERO if null
   */
  public BigDecimal getTotalRouting() {
    return totalRouting != null ? totalRouting : BigDecimal.ZERO;
  }
}

