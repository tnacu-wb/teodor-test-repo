package uk.co.whitbread.refund.processor.domain.logic;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.refund.processor.domain.model.in.RefundRequest;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.domain.ports.primary.RefundRequestProcessInPort;
import uk.co.whitbread.refund.processor.domain.ports.secondary.BasketAcknowledgeOutPort;
import uk.co.whitbread.refund.processor.domain.ports.secondary.RefundRequestProcessOutPort;

@Slf4j
@AllArgsConstructor
public class RefundRequestProcessInPortImpl implements RefundRequestProcessInPort {

  private final RefundRequestProcessOutPort refundRequestProcessOutPort;
  private final BasketAcknowledgeOutPort basketAcknowledgeOutPort;

  @Override
  public void processRefund(final RefundRequest refundRequestEvent) {
    var paymentResponse = refundRequestProcessOutPort.checkPaymentResponse(
        refundRequestEvent.getPaymentId());
    paymentResponse.ifPresentOrElse(
        response -> {
          refundRequestProcessOutPort.sendFullRefund(refundRequestEvent.getPaymentId());
          var refundResponse = refundRequestProcessOutPort.getRefundResponse(
              refundRequestEvent.getPaymentId());
          log.info("Refund request sent successfully! Refund status for paymentId={} is refund={}",
              refundRequestEvent.getPaymentId(), refundResponse.isRefunded());
          basketAcknowledgeOutPort.sendAcknowledgeMessage(refundRequestEvent.getBasketReference(),
              refundRequestEvent.getItemId(), refundRequestEvent.getRefund().getReason().toString(),
              refundResponse.isRefunded());
        },
        () -> {
          var tokenRefund = TokenRefund
              .builder()
              .requestId(UUID.randomUUID().toString())
              .hotelCode(refundRequestEvent.getHotelCode())
              .refund(refundRequestEvent.getRefund())
              .booking(refundRequestEvent.getBooking())
              .build();
          var refundResponse = handleTokenRefund(tokenRefund);
          basketAcknowledgeOutPort.sendAcknowledgeMessage(refundRequestEvent.getBasketReference(),
              refundRequestEvent.getItemId(), refundRequestEvent.getRefund().getReason().toString(),
              refundResponse.isRefunded());
        }
    );
  }

  @Override
  public RefundResponse handleTokenRefund(final TokenRefund tokenRefund) {
    var refundResponse = refundRequestProcessOutPort.sendTokenRefund(tokenRefund);
    if (refundResponse.isRefunded()) {
      log.info("Refund request sent successfully with requestId={} and refund status={}",
          refundResponse.getRequestId(), refundResponse.isRefunded());
    }
    return refundResponse;
  }

  @Override
  public void handleFailedRefund(final RefundRequest failedRefundRequestEvent) {
    log.error("Handle failed order failedRefundRequestEvent={}", failedRefundRequestEvent);
    basketAcknowledgeOutPort.sendAcknowledgeMessage(
        failedRefundRequestEvent.getBasketReference(), failedRefundRequestEvent.getItemId(),
        failedRefundRequestEvent.getRefund().getReason().toString(),
        false);
  }

}
