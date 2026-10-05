package uk.co.whitbread.basket.domain.model.rules.out;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TransactionCode {

  String pkgCode;
  String tranCode;
  Boolean vatBearing;

}
