package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BaseRateRule extends Rule {
  
  @NotEmpty
  String baseRate;
  @NotEmpty
  String promoCode;
  @NotEmpty
  String ratePlanCode;
  
  public BaseRateRule(RuleBuilder<?, ?> b, String promoCode, String baseRate, String ratePlanCode) {
    super(b);
    this.promoCode = promoCode;
    this.baseRate = baseRate;
    this.ratePlanCode = ratePlanCode;
    this.validateSelf();
  }
}
