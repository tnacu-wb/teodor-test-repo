package uk.co.whitbread.rules.manager.domain.model.in;

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
}
