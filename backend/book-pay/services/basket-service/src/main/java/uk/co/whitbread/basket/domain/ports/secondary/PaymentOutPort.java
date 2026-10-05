package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.ccuieckoh.in.UpdateTokenRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.UpdateTokenResponse;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

public interface PaymentOutPort {

  PaymentResponse getPaymentConfirmation(String paymentId);

  PaymentResponse createPayment(PaymentRequest createPayment);

  UpdateTokenResponse updateToken(UpdateTokenRequest updateTokenRequest);

  PaymentResponse createMitCcPayment(PaymentRequest createPayment);
}
