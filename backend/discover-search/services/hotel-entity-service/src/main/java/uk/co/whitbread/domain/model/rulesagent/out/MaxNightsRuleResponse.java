package uk.co.whitbread.domain.model.rulesagent.out;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaxNightsRuleResponse {

  private Integer maxNights;
  private MaxNightsRequestDetails requestDetails;
  private Date generatedAt;
}
