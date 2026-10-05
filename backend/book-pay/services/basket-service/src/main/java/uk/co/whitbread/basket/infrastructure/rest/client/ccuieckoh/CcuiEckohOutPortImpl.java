package uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.basket.domain.model.ccuieckoh.in.EckohPaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.ports.secondary.CcuiEckohOutPort;
import uk.co.whitbread.basket.infrastructure.rest.client.ccuieckoh.mapper.CcuiEckohPaymentMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class CcuiEckohOutPortImpl implements CcuiEckohOutPort {

  private final CcuiEckohPaymentMapper ccuiEckohPaymentMapper;

  @Override
  public PaymentRequest createPaymentRequest(PaymentResponse paymentResponse) {
    log.debug("Entered createPaymentRequest in CCUI Eckoh with paymentResponse={}", paymentResponse);
    return ccuiEckohPaymentMapper.toPaymentRequestModel(paymentResponse);
  }

  @Override
  public PaymentRequest createPaymentRequest(EckohPaymentRequest eckohPaymentRequest) {
    log.debug("Entered createPaymentRequest in CCUI Eckoh with eckohPaymentRequest={} ", eckohPaymentRequest);
    return ccuiEckohPaymentMapper.toPaymentRequestModel(eckohPaymentRequest);
  }
}