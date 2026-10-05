package uk.co.whitbread.basket.infrastructure.queue;

import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.infrastructure.queue.model.RefundRequestEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.RefundProducer;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;

@Slf4j
@AllArgsConstructor
public class RefundOutPortImpl implements RefundOutPort {

  private final RefundProducer refundProducer;
  private final PaymentsClient paymentsClient;
  private final PaymentRequestMapper paymentRequestMapper;
  private final PaymentResponseMapper paymentResponseMapper;

  @Override
  public void processRefund(final String basketReference, final RefundRequest refundRequest,
                                                final String paymentId) {
    var refundEvent = RefundRequestEvent.builder()
            .itemId(String.join("#", "REFUND", basketReference))
            .basketReference(basketReference)
            .hotelCode(refundRequest.getHotelCode())
            .paymentId(paymentId)
            .refund(refundRequest.getRefund())
            .booking(refundRequest.getBooking())
            .build();
    refundProducer.sendRefundRequest(refundEvent);
  }

  @Override
  public Optional<RefundResponse> processTokenRefund(RefundRequest refundRequest) {
    var tokenRefundRequest = paymentRequestMapper.toTokenRefundRequestDto(refundRequest);
    tokenRefundRequest.requestId(UUID.randomUUID().toString());
    var refundResponse = paymentsClient.sendPartialRefund(tokenRefundRequest);
    return Optional.of(paymentResponseMapper.toRefundModel(refundResponse));
  }

}
