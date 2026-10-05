package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyAmountTypeSingleCall {
  private BigDecimal amount;
  private String currencyCode;
}