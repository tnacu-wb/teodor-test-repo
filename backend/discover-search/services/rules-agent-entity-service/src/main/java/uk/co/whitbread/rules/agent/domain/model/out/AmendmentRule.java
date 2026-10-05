package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class AmendmentRule extends Rule {

  @NotEmpty
  String rateType;
  Integer arrivalDateLimit;
  @NotEmpty
  String countryCode;

  private AmendmentRule(final AmendmentRule.AmendmentRuleBuilder<?, ?> b) {
    super(b);
    this.rateType = b.rateType;
    this.arrivalDateLimit = b.arrivalDateLimit;
    this.countryCode = b.countryCode;
    this.validateSelf();
  }
}