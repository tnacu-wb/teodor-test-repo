package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
public class RateSuppressionRuleResponse extends ModelValidator<RateSuppressionRuleResponse> {

  @NotEmpty
  List<String> rateSuppressionList;
  @NotNull
  LocalDateTime generatedAt;
  @NotNull
  LocalDateTime expiryDate;

  public RateSuppressionRuleResponse(List<String> rateSuppressionList, LocalDateTime generatedAt,
      LocalDateTime expiryDate) {
    this.rateSuppressionList = rateSuppressionList;
    this.generatedAt = generatedAt;
    this.expiryDate = expiryDate;
    this.validateSelf();
  }
}
