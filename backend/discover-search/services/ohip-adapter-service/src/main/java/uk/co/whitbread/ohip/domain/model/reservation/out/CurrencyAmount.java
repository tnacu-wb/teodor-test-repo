package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAmount {
  private BigDecimal amount;
  private String currencyCode;

}
