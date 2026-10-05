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
public class MaxNightsRuleResponse extends ModelValidator<MaxNightsRuleResponse> {

  @NotNull
  Integer maxNights;
  @NotNull
  MaxNightsRequestDetails requestDetails;
  @NotNull
  LocalDateTime generatedAt;

  public MaxNightsRuleResponse(Integer maxNights, MaxNightsRequestDetails requestDetails,
      LocalDateTime generatedAt) {
    this.maxNights = maxNights;
    this.requestDetails = requestDetails;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}
