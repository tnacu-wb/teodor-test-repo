package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailsEnhancedDto {

  ReservationsDetailsResponseDto reservationsDetailsResponse;
  BillingResponseDto billing;
  private BigDecimal previousTotal;
  private BigDecimal balanceOutstanding;
  private BigDecimal newTotal;
  private BigDecimal totalCost;
  private BigDecimal amountPaid;
  private String policyCode;
  private String currencyCode;

}
