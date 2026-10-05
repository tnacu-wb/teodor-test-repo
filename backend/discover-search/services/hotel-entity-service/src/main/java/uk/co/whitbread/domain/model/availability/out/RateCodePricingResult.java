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
public class RateCodePricingResult {

  private String ratePlanCode;
  private BigDecimal totalNetAmount;
  private String currencyCode;
}
