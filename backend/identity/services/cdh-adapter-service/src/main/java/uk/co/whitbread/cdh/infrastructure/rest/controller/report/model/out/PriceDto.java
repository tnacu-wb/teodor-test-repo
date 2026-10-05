package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {

  private BigDecimal netAmount;
  private BigDecimal amount;
  private String currency;

}
