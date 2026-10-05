package uk.co.whitbread.basket.infrastructure.rest.controller.payments;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.whitbread.basket.domain.model.payments.out.InitiatePaymentResponse;
import uk.co.whitbread.basket.domain.ports.primary.PaymentInPort;
import uk.co.whitbread.basket.domain.ports.primary.RefundInPort;
import uk.co.whitbread.basket.infrastructure.rest.client.config.BasketConstants;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.AmendMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.PaymentConfirmationMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.PaymentMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentsConfirmationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.ProcessAmendRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.RefundRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.InitiatePaymentResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.PaymentConfirmationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.RefundResponseDto;

@RestController
@RequestMapping("/v1/baskets")
@RequiredArgsConstructor
public class PaymentController implements PaymentApiDocumentation {

  private final PaymentMapper paymentMapper;
  private final PaymentConfirmationMapper paymentConfirmationMapper;
  private final AmendMapper amendMapper;
  private final PaymentInPort paymentInPort;
  private final RefundInPort refundInPort;

  @Override
  @PostMapping(value = "/{basketReference}/pay", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InitiatePaymentResponseDto> initiatePaymentProcess(
      @PathVariable("basketReference") @NotNull final String basketReference,
      @RequestBody @Valid final PaymentRequestDto paymentRequestDto) {

    InitiatePaymentResponse paymentResponse;
    final var paymentRequest = paymentMapper.toPaymentReqModel(paymentRequestDto);
    if (BasketConstants.PAYPAL_TYPE.equalsIgnoreCase(paymentRequest.getPayment().getType())) {
      paymentResponse = paymentInPort.initiatePaypalPaymentProcess(basketReference, paymentRequest);
    } else {
      paymentResponse = paymentInPort.initiatePaymentProcess(basketReference, paymentRequest);
    }
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDto(paymentResponse));
  }

  @Override
  @PostMapping(value = "/{basketReference}/pp-pay", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InitiatePaymentResponseDto> initiatePaypalPaymentProcess(
      @PathVariable("basketReference") @NotNull final String basketReference,
      @RequestBody @Valid final PaymentRequestDto paymentRequestDto) {
    final var paymentRequest = paymentMapper.toPaymentRequestModel(paymentRequestDto);
    final var paymentResponse = paymentInPort
        .initiatePaypalPaymentProcess(basketReference, paymentRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toDto(paymentResponse));
  }

  @Override
  @PostMapping(value = "/{basketReference}/refund", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RefundResponseDto> refund(
      @PathVariable("basketReference") @NotNull final String basketReference, @RequestBody
      RefundRequestDto refundRequestDto) {
    final var refundRequest = paymentMapper.toRefundRequestModel(refundRequestDto);
    var refundResponse = refundInPort.refundPayment(basketReference, refundRequest);
    return refundResponse
        .map(response -> new ResponseEntity<>(paymentMapper.toRefundResponseDto(response),
            HttpStatus.ACCEPTED))
        .orElse(new ResponseEntity<>(HttpStatus.ACCEPTED));
  }

  @Override
  @PostMapping(value = "/{reference}/payment-webhook", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaymentConfirmationDto> paymentWebhook(
      @PathVariable("reference") @NotNull final String basketReference,
      @RequestBody PaymentsConfirmationDto paymentsConfirmationDto) {
    var paymentsConfirmation = paymentMapper.toModel(paymentsConfirmationDto);
    var paymentsConfirmationResponse = paymentInPort
        .paymentWebhook(basketReference, paymentsConfirmation);
    var paymentConfirmationDto = paymentConfirmationMapper.toDto(paymentsConfirmationResponse);

    paymentConfirmationDto.setBasketUri(ServletUriComponentsBuilder
        .fromCurrentRequestUri().replacePath(BASKET_PATH).path("/{basketReference}")
        .buildAndExpand(paymentConfirmationDto.getReference()).toUri());

    return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentConfirmationDto);
  }

  @Override
  @PostMapping(value = "/{reference}/process-amend")
  public void processAmend(
      @PathVariable("reference") @NotNull final String basketReference,
      @RequestBody ProcessAmendRequestDto paymentsConfirmationDto) {

    var processAmendRequest = amendMapper.toModel(paymentsConfirmationDto);
    paymentInPort.initiateProcessAmend(basketReference, processAmendRequest);
  }
}
