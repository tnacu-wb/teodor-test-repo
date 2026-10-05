package uk.co.whitbread.refund.processor.domain.ports.primary;

import uk.co.whitbread.refund.processor.domain.model.in.RefundRequest;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;

public interface RefundRequestProcessInPort {

  void processRefund(final RefundRequest refundRequestEvent);

  RefundResponse handleTokenRefund(final TokenRefund tokenRefund);

  void handleFailedRefund(final RefundRequest failedRefundRequestEvent);

}
