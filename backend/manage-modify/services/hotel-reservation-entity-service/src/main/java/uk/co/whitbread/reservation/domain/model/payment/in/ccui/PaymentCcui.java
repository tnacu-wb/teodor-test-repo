package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder
public class PaymentCcui implements SelfValidation<PaymentCcui> {

  @NotEmpty
  private String type;
  @NotEmpty
  private String subType;
  private CardCcui card;
  private BillingCcui billing;

  public PaymentCcui(String type, String subType, CardCcui card, BillingCcui billing) {
    this.type = type;
    this.subType = subType;
    this.card = card;
    this.billing = billing;
    this.validateSelf();
  }
}
