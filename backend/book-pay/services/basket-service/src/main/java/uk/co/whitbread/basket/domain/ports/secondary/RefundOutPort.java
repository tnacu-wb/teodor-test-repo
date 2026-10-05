package uk.co.whitbread.basket.domain.ports.secondary;

import java.util.Optional;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;

public interface RefundOutPort {

  void processRefund(final String basketReference, final RefundRequest refundRequest,
                                         final String paymentId);

  Optional<RefundResponse> processTokenRefund(final RefundRequest refundRequest);

}
