package uk.co.whitbread.basket.domain.ports.primary;

import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.CcuiPaymentStatusResponse;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

public interface CcuiEckohInPort {

  PaymentResponse getEckohPayment(String reference, EckohPaymentRequest eckohPaymentRequest);

  CcuiPaymentStatusResponse paymentStatus(String reference);
}