package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

public interface CcuiPaymentOutPort {
  PaymentRequest createPaymentRequest(PaymentCcuiRequest paymentCcuiRequest);

  PaymentResponse getPaymentConfirmation(String paymentId);
}
