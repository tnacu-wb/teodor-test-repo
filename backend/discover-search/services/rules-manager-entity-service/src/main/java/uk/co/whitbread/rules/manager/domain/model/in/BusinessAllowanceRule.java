package uk.co.whitbread.rules.manager.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessAllowanceRule extends Rule {

  @NotEmpty
  String pms;
  @NotEmpty
  String sourceId;
  @NotEmpty
  String targetId;
  @NotEmpty
  String aemId;
  Boolean isApplicableDaily;
}
