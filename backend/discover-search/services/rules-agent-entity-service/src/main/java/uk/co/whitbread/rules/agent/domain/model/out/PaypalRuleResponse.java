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
public class PaypalRuleResponse extends ModelValidator<PaypalRuleResponse> {

  @NotNull
  Boolean isPayPalPaymentEnabled;

  @NotNull
  LocalDateTime generatedAt;

  public PaypalRuleResponse(Boolean isPayPalPaymentEnabled,
                            LocalDateTime generatedAt) {
    this.isPayPalPaymentEnabled = isPayPalPaymentEnabled;
    this.generatedAt = generatedAt;
    this.validateSelf();
  }
}
