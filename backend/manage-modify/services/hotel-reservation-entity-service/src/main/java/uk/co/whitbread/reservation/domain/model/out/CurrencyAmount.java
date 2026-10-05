package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CurrencyAmount {
  private BigDecimal amount;
  private String currencyCode;

}
