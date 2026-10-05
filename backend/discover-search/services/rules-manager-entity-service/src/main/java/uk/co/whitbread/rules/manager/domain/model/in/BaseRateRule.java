package uk.co.whitbread.rules.manager.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BaseRateRule extends Rule {
  
  @NotEmpty
  String ratePlanCode;
  @NotEmpty
  String promoCode;
  @NotEmpty
  String baseRate;
}
