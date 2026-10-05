package uk.co.whitbread.domain.model.availability.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPrice {

  private String date;
  private BigDecimal netPrice;
  private BigDecimal roomNetPrice;
  private BigDecimal grossPrice;
  private BigDecimal effectiveRate;

}
