package uk.co.whitbread.rules.manager.domain.model.in;

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

}
