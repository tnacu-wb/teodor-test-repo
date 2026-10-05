package uk.co.whitbread.ohip.domain.model.rates.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class RatePlanBasedOnRate {

  private DynamicBaseRate dynamicBaseRate;
  private String basedOnRateType;


}
