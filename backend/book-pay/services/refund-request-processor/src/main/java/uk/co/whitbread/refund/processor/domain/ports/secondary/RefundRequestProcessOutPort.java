package uk.co.whitbread.refund.processor.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.PaymentResponse;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;

public interface RefundRequestProcessOutPort {

  void sendFullRefund(String paymentId);

  RefundResponse getRefundResponse(String paymentId);

  Optional<PaymentResponse> checkPaymentResponse(String paymentId);

  RefundResponse sendTokenRefund(TokenRefund tokenRefund);
}
