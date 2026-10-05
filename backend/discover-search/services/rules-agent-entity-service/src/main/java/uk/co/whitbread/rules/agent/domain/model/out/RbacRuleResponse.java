package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class RbacRuleResponse extends ModelValidator<RbacRuleResponse> {

  @NotNull
  Boolean hasAccess;
  @NotNull
  LocalDateTime generatedAt;

  public RbacRuleResponse(Boolean hasAccess, LocalDateTime generatedAt) {
    this.hasAccess = hasAccess;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}
