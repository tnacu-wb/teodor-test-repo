package uk.co.whitbread.rules.agent.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class RbacRuleRequest extends ModelValidator<RbacRuleRequest> {

  @NotEmpty
  String resourceId;
  @NotEmpty
  String roleId;

  public RbacRuleRequest(String resourceId, String roleId) {
    this.resourceId = resourceId;
    this.roleId = roleId;
    this.validateSelf();
  }
}
