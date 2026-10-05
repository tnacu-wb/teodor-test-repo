package uk.co.whitbread.rules.agent.domain.model.out;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class VatRule extends Rule {

  String vatRegion;
  String tranCode;
  String pkgCode;
  Boolean vatBearing;

}
