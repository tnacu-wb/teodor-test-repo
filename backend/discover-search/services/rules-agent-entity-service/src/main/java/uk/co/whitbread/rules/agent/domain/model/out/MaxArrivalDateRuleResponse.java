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
public class MaxArrivalDateRuleResponse extends ModelValidator<MaxArrivalDateRuleResponse> {

  @NotNull
  Integer maxArrivalDate;
  @NotNull
  MaxArrivalDateRequestDetails requestDetails;
  @NotNull
  LocalDateTime generatedAt;

  public MaxArrivalDateRuleResponse(Integer maxNights, MaxArrivalDateRequestDetails requestDetails,
      LocalDateTime generatedAt) {
    this.maxArrivalDate = maxNights;
    this.requestDetails = requestDetails;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}
