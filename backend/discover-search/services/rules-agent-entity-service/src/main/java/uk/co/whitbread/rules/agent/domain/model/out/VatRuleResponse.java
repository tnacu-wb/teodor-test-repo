package uk.co.whitbread.rules.agent.domain.model.out;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class VatRuleResponse {

  String vatRegion;
  List<TransactionCode> tranCodes;
  LocalDateTime generatedAt;

}
