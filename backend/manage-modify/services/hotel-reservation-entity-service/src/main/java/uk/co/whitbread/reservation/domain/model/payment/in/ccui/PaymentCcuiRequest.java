package uk.co.whitbread.reservation.domain.model.payment.in.ccui;


import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;


@Data
@Builder
public class PaymentCcuiRequest implements SelfValidation<PaymentCcuiRequest> {

  private String requestId;
  private PaymentCcui payment;

  public PaymentCcuiRequest(String requestId, PaymentCcui payment) {
    this.requestId = requestId;
    this.payment = payment;
    this.validateSelf();
  }
}
