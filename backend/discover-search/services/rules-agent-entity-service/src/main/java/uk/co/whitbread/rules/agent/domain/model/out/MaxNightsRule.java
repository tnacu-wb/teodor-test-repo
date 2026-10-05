package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MaxNightsRule extends Rule {

  @NotEmpty
  String channelId;
  @NotNull
  Integer maxNights;

  private MaxNightsRule(final MaxNightsRule.MaxNightsRuleBuilder<?, ?> b) {
    super(b);
    this.channelId = b.channelId;
    this.maxNights = b.maxNights;
    this.validateSelf();
  }
}
