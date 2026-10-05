package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RateSuppressionRuleResponseDto {

  @JsonProperty("rate-suppression-list")
  List<String> rateSuppressionList;
  LocalDateTime generatedAt;
  LocalDateTime expiryDate;
}
