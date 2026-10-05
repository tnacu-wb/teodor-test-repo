package uk.co.whitbread.basket.domain.ports.primary;

import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.InitiatePaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

public interface PaymentInPort {

  InitiatePaymentResponse initiatePaymentProcess(String basketReference,
                                                 PaymentRequest paymentRequest);

  InitiatePaymentResponse initiatePaypalPaymentProcess(String basketReference,
                                                 PaymentRequest paymentRequest);

  PaymentResponse paymentWebhook(String basketReference, PaymentsConfirmation paymentsConfirmation);

  void initiateProcessAmend(final String basketReference, PaymentsConfirmation paymentsConfirmation);
}
