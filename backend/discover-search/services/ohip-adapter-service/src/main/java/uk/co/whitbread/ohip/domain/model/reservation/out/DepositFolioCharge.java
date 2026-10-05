package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioCharge {
  private String transactionCode;
  private Integer quantity;
  private String reference;
  private CurrencyAmount currencyAmount;

}
