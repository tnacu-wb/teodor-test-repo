package uk.co.whitbread.ohip.domain.model.rules.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseRateRuleResponse {
  
  private String promoCode;
  private String ratePlanCode;
  private String baseRate;
}
