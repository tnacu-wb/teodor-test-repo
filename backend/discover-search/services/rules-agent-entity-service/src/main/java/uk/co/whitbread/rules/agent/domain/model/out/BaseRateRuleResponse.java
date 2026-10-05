package uk.co.whitbread.rules.agent.domain.model.out;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import uk.co.whitbread.rules.agent.domain.model.validation.ModelValidator;

@Value
@Builder
@EqualsAndHashCode(callSuper = false)
public class BaseRateRuleResponse extends ModelValidator<BaseRateRuleResponse> {
  
  @NotNull
  String promoCode;
  
  @NotNull
  String ratePlanCode;
  
  @NotNull
  String baseRate;
  
  public BaseRateRuleResponse(String promoCode, String ratePlanCode, String baseRate) {
    this.promoCode = promoCode;
    this.ratePlanCode = ratePlanCode;
    this.baseRate = baseRate;
    this.validateSelf();
  }
}
