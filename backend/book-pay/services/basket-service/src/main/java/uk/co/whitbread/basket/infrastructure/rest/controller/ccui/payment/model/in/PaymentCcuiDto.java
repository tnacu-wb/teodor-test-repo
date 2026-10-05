package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class PaymentCcuiDto implements SelfValidation<PaymentCcuiDto> {

  @NotEmpty
  private String type;
  @NotEmpty
  private String subType;
  private CardCcuiDto card;
  private BillingCcuiDto billing;

  public PaymentCcuiDto(String type, String subType, CardCcuiDto card, BillingCcuiDto billing) {
    this.type = type;
    this.subType = subType;
    this.card = card;
    this.billing = billing;
    this.validateSelf();
  }
}
