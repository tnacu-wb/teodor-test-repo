package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class RbacRule extends Rule {

  @NotEmpty
  String resourceId;
  @NotEmpty
  String roleId;
  @NotNull
  Boolean hasAccess;

  private RbacRule(final RbacRule.RbacRuleBuilder<?, ?> b) {
    super(b);
    this.resourceId = b.resourceId;
    this.roleId = b.roleId;
    this.hasAccess = b.hasAccess;
    this.validateSelf();
  }
}