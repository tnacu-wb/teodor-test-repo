package uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.PaymentCcuiRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiPaymentOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class CcuiPaymentOutPortImpl implements CcuiPaymentOutPort {

  private final PaymentsClient paymentsClient;

  private final CcuiEckohPaymentMapper ccuiEckohPaymentMapper;
  private final CcuiEckohPaymentResponseMapper ccuiEckohPaymentResponseMapper;

  @Override
  public PaymentRequest createPaymentRequest(PaymentCcuiRequest paymentCcuiRequest) {
    log.debug("Entered createPaymentRequest for CCUI payment with paymentCcuiRequest={}", paymentCcuiRequest);
    return ccuiEckohPaymentMapper.toPaymentRequestModel(paymentCcuiRequest);
  }

  @Override
  public PaymentResponse getPaymentConfirmation(String paymentId) {
    log.debug("Entered getPaymentConfirmation with paymentId={}", paymentId);
    var clientResponse = paymentsClient.getPaymentConfirmation(paymentId);
    return ccuiEckohPaymentResponseMapper.toModel(clientResponse);
  }
}
