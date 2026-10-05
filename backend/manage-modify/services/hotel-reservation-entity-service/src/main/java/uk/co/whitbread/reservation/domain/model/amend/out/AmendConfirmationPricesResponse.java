package uk.co.whitbread.reservation.domain.model.amend.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendConfirmationPricesResponse {

  private BigDecimal previousTotal;
  private BigDecimal newTotalCost;
  private BigDecimal outstandingBalance;
}
