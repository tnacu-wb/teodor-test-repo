package uk.co.whitbread.basket.domain.model.rules.out;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VatRuleResponse {

  String vatRegion;
  List<TransactionCode> tranCodes;
  LocalDateTime generatedAt;

}
