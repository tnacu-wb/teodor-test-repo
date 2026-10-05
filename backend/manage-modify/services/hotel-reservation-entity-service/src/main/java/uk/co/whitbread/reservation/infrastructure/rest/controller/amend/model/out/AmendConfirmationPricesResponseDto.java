package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AmendConfirmationPricesResponseDto {

  private BigDecimal previousTotal;
  private BigDecimal newTotalCost;
  private BigDecimal outstandingBalance;

}
