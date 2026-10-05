package uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.InitiatePaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentsConfirmationDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.RefundRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.InitiatePaymentResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.RefundResponseDto;

@Mapper(componentModel = "spring", uses = {
    AddressMapperRequest.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

  InitiatePaymentResponseDto toDto(InitiatePaymentResponse paymentResponse);

  PaymentRequest toPaymentRequestModel(PaymentRequestDto paymentRequestDto);

  RefundRequest toRefundRequestModel(RefundRequestDto refundRequestDto);

  PaymentsConfirmation toModel(PaymentsConfirmationDto paymentConfirmationDto);

  RefundResponseDto toRefundResponseDto(RefundResponse refundResponse);

  /**
   * Converts PaymentRequestDto to PaymentRequest with PIBA to PIBA CNP transformation.
   * When isCiol is true and payment type is PIBA, sets pibaCardPresent to false.
   *
   * @param paymentRequestDto the payment request DTO
   * @return the converted PaymentRequest model
   */
  default PaymentRequest toPaymentReqModel(PaymentRequestDto paymentRequestDto) {
    var isCiol = paymentRequestDto.getIsCiol();
    var paymentDto = paymentRequestDto.getPayment();
    if (Boolean.TRUE.equals(isCiol)
        && paymentDto != null
        && "PIBA".equalsIgnoreCase(paymentDto.getType())) {
      paymentDto.setPibaCardPresent(false);
      paymentRequestDto.setPayment(paymentDto);
    }

    return toPaymentRequestModel(paymentRequestDto);
  }

}
