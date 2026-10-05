package uk.co.whitbread.rules.manager.domain.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class VatRule extends Rule {

  @NotEmpty
  String vatRegion;
  @NotEmpty
  String tranCode;
  @NotEmpty
  String description;
  String pkgCode;
  Boolean vatBearing;

}
