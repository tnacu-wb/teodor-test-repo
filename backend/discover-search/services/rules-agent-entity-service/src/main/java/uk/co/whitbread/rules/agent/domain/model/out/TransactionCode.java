package uk.co.whitbread.rules.agent.domain.model.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionCode {

  String pkgCode;
  String tranCode;
  Boolean vatBearing;

}
