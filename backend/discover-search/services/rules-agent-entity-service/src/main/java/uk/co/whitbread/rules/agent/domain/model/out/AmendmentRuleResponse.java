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
public class AmendmentRuleResponse extends ModelValidator<AmendmentRuleResponse> {

  @NotNull
  Boolean isAmendable;
  @NotNull
  AmendmentRequestDetails requestDetails;
  @NotNull
  LocalDateTime generatedAt;

  public AmendmentRuleResponse(Boolean isAmendable, AmendmentRequestDetails requestDetails,
      LocalDateTime generatedAt) {
    this.isAmendable = isAmendable;
    this.requestDetails = requestDetails;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}
