package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateCodePricingRsDto {

  private String ratePlanCode;
  private BigDecimal totalNetAmount;
  private String currencyCode;
}
