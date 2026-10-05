package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VatRuleResponseDto {

  String vatRegion;
  List<TransactionCodeDto> tranCodes;
  LocalDateTime generatedAt;

}
