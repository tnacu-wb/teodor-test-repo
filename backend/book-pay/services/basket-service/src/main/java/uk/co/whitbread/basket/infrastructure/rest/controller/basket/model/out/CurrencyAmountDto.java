package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CurrencyAmountDto {

  private BigDecimal amount;
  private String currencyCode;
}
