package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioChargeDto {
  private CurrencyAmountDto currencyAmount;
  private Integer quantity;
  private String reference;
  private String transactionCode;
}