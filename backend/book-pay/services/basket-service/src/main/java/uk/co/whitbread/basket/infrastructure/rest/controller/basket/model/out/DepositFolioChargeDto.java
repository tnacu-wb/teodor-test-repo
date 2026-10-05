package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DepositFolioChargeDto {

  private String transactionCode;
  private Integer quantity;
  private String reference;
  private CurrencyAmountDto currencyAmount;

}
