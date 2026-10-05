package uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRuleResponseDto {

  private String sourceId;
  private List<String> ratePlanSets;
  private ChannelRuleRequestDetailsDto requestDetails;
  private LocalDateTime generatedAt;
}