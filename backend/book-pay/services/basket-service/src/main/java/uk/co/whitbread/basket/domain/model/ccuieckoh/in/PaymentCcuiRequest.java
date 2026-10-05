package uk.co.whitbread.basket.domain.model.ccuieckoh.in;


import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class PaymentCcuiRequest implements SelfValidation<PaymentCcuiRequest> {

  private String requestId;
  private PaymentCcui payment;
  private Booking booking;

  public PaymentCcuiRequest(String requestId, PaymentCcui payment, Booking booking) {
    this.requestId = requestId;
    this.payment = payment;
    this.booking = booking;
    this.validateSelf();
  }
}
