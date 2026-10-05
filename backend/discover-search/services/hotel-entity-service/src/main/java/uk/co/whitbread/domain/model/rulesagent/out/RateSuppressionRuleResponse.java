package uk.co.whitbread.domain.model.rulesagent.out;

import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateSuppressionRuleResponse {

  private List<String> rateSuppressionList;
  private Date generatedAt;
  private Date expiryDate;
}
