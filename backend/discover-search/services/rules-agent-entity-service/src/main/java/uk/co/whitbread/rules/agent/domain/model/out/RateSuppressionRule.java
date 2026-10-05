package uk.co.whitbread.rules.agent.domain.model.out;


import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RateSuppressionRule extends Rule {

  @NotNull
  String rateType;
  @NotNull
  Short priority;

  private RateSuppressionRule(final RateSuppressionRule.RateSuppressionRuleBuilder<?, ?> b) {
    super(b);
    this.rateType = b.rateType;
    this.priority = b.priority;
    this.validateSelf();
  }
}
