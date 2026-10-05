package uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAmountTypeDto {

  private BigDecimal amount;
  private String currencyCode;
}
