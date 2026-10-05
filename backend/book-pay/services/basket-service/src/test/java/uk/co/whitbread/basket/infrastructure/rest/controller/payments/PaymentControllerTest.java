package uk.co.whitbread.basket.infrastructure.rest.controller.payments;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.InitiatePaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.ports.primary.PaymentInPort;
import uk.co.whitbread.basket.domain.ports.primary.RefundInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.AmendMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.mapper.PaymentMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.AddressDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BillingDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BookingDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.PaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.ProcessAmendRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.RefundDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.RefundRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.RefundType;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out.RefundResponseDto;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

  @InjectMocks
  private PaymentController paymentController;

  @Mock
  private PaymentInPort paymentInPort;

  @Mock(answer = org.mockito.Answers.CALLS_REAL_METHODS)
  private PaymentMapper paymentMapper;

  @Mock
  private RefundInPort refundInPort;

  @Mock
  private AmendMapper amendMapper;


  @Test
  void refund_ShouldReturnOk() {
    // Asssert
    when(paymentMapper.toRefundRequestModel(any())).thenReturn(RefundRequest.builder().build());
    when(refundInPort.refundPayment(any(), any())).thenReturn(Optional.of(RefundResponse.builder().build()));
    when(paymentMapper.toRefundResponseDto(any())).thenReturn(RefundResponseDto.builder().build());

    //Act
    var result = paymentController.refund("basketRef", mockRefundRequestDto());

    //Assert

    verify(refundInPort, times(1)).refundPayment(eq("basketRef"), any());
    assertThat(result, notNullValue());

  }


  private RefundRequestDto mockRefundRequestDto(){
    return RefundRequestDto.builder()
        .hotelCode("hotelCode")
        .refund(RefundDto.builder().build())
        .booking(BookingDto.builder().build())
        .refundType(RefundType.FULL)
        .build();
  }

  @Test
  void processAmend__ShouldReturnOk(){
    // Assert
    when(amendMapper.toModel(any())).thenReturn(new PaymentsConfirmation());

    // Act
    paymentController.processAmend("BasketRef",
        new ProcessAmendRequestDto("en", "BB", PaymentOption.PAY_ON_ARRIVAL.toString(), "email", "jane.doe@wb.com", null));

    //Assert
    verify(paymentInPort, times(1)).initiateProcessAmend(eq("BasketRef"), any());

  }

  @Test
  void processAmend_WithToken_ShouldReturnOk(){
    // Assert
    when(amendMapper.toModel(any())).thenReturn(new PaymentsConfirmation());

    // Act
    paymentController.processAmend("BasketRef",
        new ProcessAmendRequestDto("en", "BB", PaymentOption.PAY_ON_ARRIVAL.toString(), "email", "jane.doe@wb.com", "dummyToken"));

    //Assert
    verify(paymentInPort, times(1)).initiateProcessAmend(eq("BasketRef"), any());

  }

  @Test
  void initiatePaymentProcess_ShouldReturnCreated() {
    // Arrange
    PaymentRequestDto paymentRequestDto = mockPaymentRequestDto();
    PaymentRequest paymentRequest = PaymentRequest.builder()
        .payment(Payment.builder().type("CC").subType("VISA").build()).build();
    when(paymentMapper.toPaymentRequestModel(any(PaymentRequestDto.class))).thenReturn(
        paymentRequest);
    when(paymentInPort.initiatePaymentProcess(any(), any())).thenReturn(
        InitiatePaymentResponse.builder().build());
    when(paymentMapper.toDto(any())).thenReturn(null);

    // Act
    var result = paymentController.initiatePaymentProcess("basketRef", paymentRequestDto);

    // Assert
    verify(paymentInPort, times(1)).initiatePaymentProcess(eq("basketRef"), any());
    assertThat(result, notNullValue());
  }

  private PaymentRequestDto mockPaymentRequestDto() {
    BillingDto billingDto = BillingDto.builder()
        .address(AddressDto.builder().addressLine1("line1").postalCode("1234").build())
        .build();
    PaymentDto paymentDto = PaymentDto.builder()
        .billing(billingDto)
        .subType("subType")
        .build();
    return PaymentRequestDto.builder()
        .booking(BookingDto.builder().build())
        .payment(paymentDto)
        .build();
  }
}