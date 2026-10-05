package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

public interface CcuiEckohOutPort {

  PaymentRequest createPaymentRequest(PaymentResponse paymentResponse);

  PaymentRequest createPaymentRequest(EckohPaymentRequest paymentResponse);
}
