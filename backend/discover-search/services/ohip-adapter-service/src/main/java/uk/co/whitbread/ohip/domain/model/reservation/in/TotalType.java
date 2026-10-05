package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalType {

  private BigDecimal amountBeforeTax;
  private String currencyCode;
}
