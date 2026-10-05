package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseDto {

  private BigDecimal amountBeforeTax;
  private String currencyCode;

}
