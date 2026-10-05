package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendmentRuleResponseDto {

  private Boolean isAmendable;
  private AmendmentRequestDetailsDto requestDetails;
  private LocalDateTime generatedAt;
}
