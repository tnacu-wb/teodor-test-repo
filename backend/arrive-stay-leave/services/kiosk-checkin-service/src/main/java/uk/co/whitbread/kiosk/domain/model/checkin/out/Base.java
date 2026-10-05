package uk.co.whitbread.kiosk.domain.model.checkin.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Base {

  private BigDecimal amountBeforeTax;
  private String currencyCode;

}
