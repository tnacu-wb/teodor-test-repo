package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionCodeDto {

  String pkgCode;
  String tranCode;
  Boolean vatBearing;

}
