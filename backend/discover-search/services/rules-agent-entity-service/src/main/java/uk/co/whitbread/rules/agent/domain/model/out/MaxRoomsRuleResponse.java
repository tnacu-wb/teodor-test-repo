package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class MaxRoomsRuleResponse extends ModelValidator<MaxRoomsRuleResponse> {

  @NotNull
  Integer maxRooms;
  @NotNull
  MaxRoomsRequestDetails requestDetails;
  @NotNull
  LocalDateTime generatedAt;

  public MaxRoomsRuleResponse(Integer maxRooms, MaxRoomsRequestDetails requestDetails,
      LocalDateTime generatedAt) {
    this.maxRooms = maxRooms;
    this.requestDetails = requestDetails;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}