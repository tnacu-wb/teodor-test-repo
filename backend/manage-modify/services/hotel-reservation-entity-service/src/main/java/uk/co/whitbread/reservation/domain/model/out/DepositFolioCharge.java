package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DepositFolioCharge {

  private String transactionCode;
  private Integer quantity;
  private String reference;
  private CurrencyAmount currencyAmount;

}
