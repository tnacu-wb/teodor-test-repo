package uk.co.whitbread.basket.infrastructure.rest.client.payments;

import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.UpdateTokenRequest;
import uk.co.whitbread.basket.domain.model.ccuieckoh.out.UpdateTokenResponse;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.UpdateTokenMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentOutPortImpl implements PaymentOutPort {

  private final PaymentsClient paymentsClient;
  private final PaymentResponseMapper paymentResponseMapper;
  private final PaymentRequestMapper paymentRequestMapper;

  private final UpdateTokenMapper updateTokenMapper;

  @Override
  public PaymentResponse getPaymentConfirmation(String paymentId) {
    log.debug("Entered getPaymentConfirmation with paymentId={}", paymentId);
    var clientResponse = paymentsClient.getPaymentConfirmation(paymentId);
    return paymentResponseMapper.toModel(clientResponse);
  }

  @Override
  public PaymentResponse createPayment(PaymentRequest createPayment) {
    log.debug("Entered createPayment with paymentRequest={}", createPayment);
    var paymentRequest = paymentRequestMapper.toPaymentRequestDto(createPayment);
    var clientPaymentResponse = paymentsClient.createPayment(paymentRequest);
    return paymentResponseMapper.toModel(clientPaymentResponse);
  }

  @Override
  public PaymentResponse createMitCcPayment(PaymentRequest createPayment) {
    log.debug("Entered MIT_CC createPayment requestId={} tmpBasketRef={}",
        createPayment != null ? createPayment.getRequestId() : null,
        createPayment != null ? sanitize(createPayment.getTmpBasketRef()) : null);
    var paymentRequest = paymentRequestMapper.toPaymentRequestDto(createPayment);
    var clientPaymentResponse = paymentsClient.createMitCcPayment(paymentRequest);
    return paymentResponseMapper.toModel(clientPaymentResponse);
  }

  @Override
  public UpdateTokenResponse updateToken(UpdateTokenRequest updateTokenRequest) {
    var safeLog = String.format(
        "UpdateTokenRequest{requestId=%s, cardHolderFirstName=%s, cardHolderLastName=%s}",
        updateTokenRequest.getRequestId(), updateTokenRequest.getCardHolderFirstName(),
        updateTokenRequest.getCardHolderLastName());
    log.debug("Entered updateToken with updateTokenRequest={}", safeLog);
    var updateTokenRequestDto = updateTokenMapper.toDto(updateTokenRequest);
    var updateTokenResponseDto = paymentsClient.updateToken(updateTokenRequestDto);
    return updateTokenMapper.toModel(updateTokenResponseDto);
  }
}
