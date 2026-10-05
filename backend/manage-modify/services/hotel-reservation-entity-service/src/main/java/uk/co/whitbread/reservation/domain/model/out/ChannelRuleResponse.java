package uk.co.whitbread.reservation.domain.model.out;

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
public class ChannelRuleResponse {

  private String sourceId;
  private List<String> ratePlanSets;
  private ChannelRuleRequestDetails requestDetails;
  private LocalDateTime generatedAt;
}
