package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceDto {

  private String date;
  private BigDecimal netPrice;
  private BigDecimal roomNetPrice;
  private BigDecimal grossPrice;
  private BigDecimal effectiveRate;

}
