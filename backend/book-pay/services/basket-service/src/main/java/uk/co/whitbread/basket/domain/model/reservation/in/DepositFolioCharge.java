package uk.co.whitbread.basket.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioCharge {

  private CurrencyAmount currencyAmount;

  private Integer quantity;

  private String reference;

  private String transactionCode;
}