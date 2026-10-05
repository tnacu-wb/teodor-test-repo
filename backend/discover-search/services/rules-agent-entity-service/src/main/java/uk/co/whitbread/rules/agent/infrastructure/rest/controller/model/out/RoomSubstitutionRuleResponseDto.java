package uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoomSubstitutionRuleResponseDto {

  @JsonProperty("substitution-list")
  List<RoomSubstitutionDto> substitutionList;
  RoomSubstitutionRequestDetailsDto requestDetails;
  LocalDateTime generatedAt;

}
