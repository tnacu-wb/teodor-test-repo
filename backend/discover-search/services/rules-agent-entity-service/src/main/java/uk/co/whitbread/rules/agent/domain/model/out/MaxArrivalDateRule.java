package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MaxArrivalDateRule extends Rule {

  @NotEmpty
  String channelId;
  @NotNull
  Integer maxArrivalDate;

  private MaxArrivalDateRule(final MaxArrivalDateRule.MaxArrivalDateRuleBuilder<?, ?> b) {
    super(b);
    this.channelId = b.channelId;
    this.maxArrivalDate = b.maxArrivalDate;
    this.validateSelf();
  }
}
