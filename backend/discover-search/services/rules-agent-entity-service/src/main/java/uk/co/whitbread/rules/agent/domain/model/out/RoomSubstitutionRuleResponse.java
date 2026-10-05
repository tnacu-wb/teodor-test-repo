package uk.co.whitbread.rules.agent.domain.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class RoomSubstitutionRuleResponse {

  @JsonProperty("substitution-list")
  List<RoomSubstitution> substitutionList;
  RoomSubstitutionRequestDetails requestDetails;
  LocalDateTime generatedAt;

}
