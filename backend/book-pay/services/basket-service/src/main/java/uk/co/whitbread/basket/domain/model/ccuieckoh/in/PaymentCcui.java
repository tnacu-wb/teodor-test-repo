package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class PaymentCcui implements SelfValidation<PaymentCcui> {

  @NotEmpty
  private String type;
  @NotEmpty
  private String subType;
  private String environment;
  private CardCcui card;

  private Amount amount;
  private BillingCcui billing;

  public PaymentCcui(String type, String subType, String environment, CardCcui card,
      Amount amount, BillingCcui billing) {
    this.type = type;
    this.subType = subType;
    this.environment = environment;
    this.card = card;
    this.amount = amount;
    this.billing = billing;
    this.validateSelf();
  }
}
