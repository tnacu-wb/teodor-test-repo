package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendSummaryDetails {
  private BigDecimal charitable;
  private BigDecimal previousTotal;
  private BigDecimal balancePaid;
  private BigDecimal payOnArrival;
  private BigDecimal refund;
  private BigDecimal nonRefundable;
  private BigDecimal totalCost;
  private BigDecimal balanceAuthorised;
  private PaymentOptions paymentOptions;
  private NavigationOptions navigationOptions;
  private PaymentCardDetails paymentCardDetails;
}
