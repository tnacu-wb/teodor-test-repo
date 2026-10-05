package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.PaymentResponse;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.domain.ports.secondary.RefundRequestProcessOutPort;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.InvalidPaymentException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.mapper.PaymentResponseMapper;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.mapper.RefundResponseMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundRequestProcessOutPortImpl implements RefundRequestProcessOutPort {

  private final PaymentsClient paymentsClient;
  private final RefundResponseMapper refundResponseMapper;
  private final PaymentResponseMapper paymentResponseMapper;

  @Override
  public void sendFullRefund(String paymentId) {
    log.info("Entered sendRefund with paymentId={}", paymentId);
    paymentsClient.sendFullRefund(paymentId);
  }

  @Override
  public RefundResponse getRefundResponse(String paymentId) {
    var paymentResponse = paymentsClient.getPaymentResponse(paymentId);
    return refundResponseMapper.toModelFromPaymentResponse(paymentResponse);
  }

  @Override
  public Optional<PaymentResponse> checkPaymentResponse(String paymentId) {
    if (StringUtils.isEmpty(paymentId)) {
      return Optional.empty();
    }
    PaymentResponseDto paymentResponse;
    try {
      paymentResponse = paymentsClient.getPaymentResponse(paymentId);
    } catch (InvalidPaymentException ex) {
      log.error("getPaymentResponse(): in case of 4xx calls, we should return Optional empty.");
      return Optional.empty();
    }
    return Optional.of(paymentResponseMapper.toModel(paymentResponse));
  }

  @Override
  public RefundResponse sendTokenRefund(TokenRefund tokenRefund) {
    log.info("Entered sendTokenRefund with refundRequest - requestId ={}", tokenRefund.getRequestId());
    var refundResponse =  paymentsClient.sendTokenRefund(tokenRefund);
    log.info("Finished sendTokenRefund with refundResponse={}", refundResponse);
    return refundResponseMapper.toModelFromRefundResponse(refundResponse);
  }

}
