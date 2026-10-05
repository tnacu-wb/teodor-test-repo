package uk.co.whitbread.basket.domain.model.payments.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Amount {

  private String currency;
  private BigDecimal minorUnits;
}
