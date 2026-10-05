package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.refund.processor.domain.model.in.Amount;
import uk.co.whitbread.refund.processor.domain.model.in.Booking;
import uk.co.whitbread.refund.processor.domain.model.in.BusinessSite;
import uk.co.whitbread.refund.processor.domain.model.in.Card;
import uk.co.whitbread.refund.processor.domain.model.in.PaymentType;
import uk.co.whitbread.refund.processor.domain.model.in.Refund;
import uk.co.whitbread.refund.processor.domain.model.in.RefundReason;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.PaymentResponse;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.RefundResponseDto;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.ErrorCode;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.InvalidPaymentException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.mapper.PaymentResponseMapper;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.mapper.RefundResponseMapper;

@ExtendWith(MockitoExtension.class)
class RefundRequestProcessOutPortImplTest {

  @Mock
  private PaymentsClient paymentsClient;
  @Mock
  private RefundResponseMapper refundResponseMapper;
  @Mock
  private PaymentResponseMapper paymentResponseMapper;
  private RefundRequestProcessOutPortImpl refundRequestProcessOutPortImpl;

  @BeforeEach
  public void before() {
    refundRequestProcessOutPortImpl =
        new RefundRequestProcessOutPortImpl(paymentsClient, refundResponseMapper, paymentResponseMapper);
  }

  @Test
  void testSendRefund_success() {
    //Arrange
    var paymentId = "abc";
    //Act
    refundRequestProcessOutPortImpl.sendFullRefund(paymentId);

    // Assert
    verify(paymentsClient).sendFullRefund(paymentId);
  }

  @Test
  void testGetRefundResponse() {
    //Arrange
    var paymentResponseDto = mockPaymentResponseDto();
    var refundResponse = mockRefundResponse();

    when(paymentsClient.getPaymentResponse(paymentResponseDto.getPaymentId())).thenReturn(paymentResponseDto);
    when(refundResponseMapper.toModelFromPaymentResponse(any())).thenReturn(refundResponse);

    //Act
    var response = refundRequestProcessOutPortImpl.getRefundResponse(paymentResponseDto.getPaymentId());

    // Assert
    verify(paymentsClient).getPaymentResponse(paymentResponseDto.getPaymentId());
    assertEquals(response.getPaymentId(), refundResponse.getPaymentId());
  }

  @Test
  void testCheckPaymentResponseNotEmpty() {
    //Arrange
    var paymentResponseDto = mockPaymentResponseDto();
    PaymentResponse paymentResponse = mockPaymentResponse();

    when(paymentsClient.getPaymentResponse(paymentResponseDto.getPaymentId())).thenReturn(paymentResponseDto);
    when(paymentResponseMapper.toModel(any())).thenReturn(paymentResponse);

    //Act
    var response = refundRequestProcessOutPortImpl
        .checkPaymentResponse(paymentResponseDto.getPaymentId());

    // Assert
    verify(paymentsClient).getPaymentResponse(paymentResponseDto.getPaymentId());
    assertTrue(response.isPresent());
    assertEquals(response.get().getPaymentId(), paymentResponseDto.getPaymentId());
  }

  @Test
  void testCheckPaymentResponseEmpty() {
    //Arrange
    var paymentResponseDto = mockPaymentResponseDto();

    when(paymentsClient.getPaymentResponse(paymentResponseDto.getPaymentId()))
        .thenThrow(new InvalidPaymentException(ErrorCode.INVALID_PAYMENT_RESPONSE_EXCEPTION,
            "Improper call of Payment Service."));

    //Act
    var response = refundRequestProcessOutPortImpl
        .checkPaymentResponse(paymentResponseDto.getPaymentId());

    // Assert
    verify(paymentsClient).getPaymentResponse(paymentResponseDto.getPaymentId());
    assertTrue(response.isEmpty());
  }

  @Test
  void testSendTokenRefund() {
    // Arrange
    var tokenRefundRequest = mockTokenRefund();
    var refundResponseDto = mockRefundResponseDto();
    var refundResponse = mockRefundResponse();
    when(paymentsClient.sendTokenRefund(any(TokenRefund.class))).thenReturn(refundResponseDto);
    when(refundResponseMapper.toModelFromRefundResponse(refundResponseDto)).thenReturn(refundResponse);

    // Act
    var response = refundRequestProcessOutPortImpl.sendTokenRefund(tokenRefundRequest);

    // Assert
    verify(paymentsClient).sendTokenRefund(any(TokenRefund.class));
    assertNotNull(response);
  }

  private PaymentResponseDto mockPaymentResponseDto() {
    var paymentResponseDto = new PaymentResponseDto();
    paymentResponseDto.setPaymentId("123456789");
    paymentResponseDto.setRefunded(true);
    return paymentResponseDto;
  }

  private PaymentResponse mockPaymentResponse() {
    PaymentResponse paymentResponse = new PaymentResponse();
    paymentResponse.setPaymentId("123456789");
    paymentResponse.setRefunded(true);
    return paymentResponse;
  }

  private TokenRefund mockTokenRefund() {
    return TokenRefund
        .builder()
        .requestId("ABCD#12345")
        .hotelCode("LONEUS")
        .refund(Refund
            .builder()
            .type(PaymentType.CARD)
            .amount(Amount
                .builder()
                .currency("GBP")
                .minorUnits(BigDecimal.TEN)
                .build())
            .card(Card
                .builder()
                .token("4216333880397891103")
                .expiryYear("26")
                .expiryMonth("03")
                .build())
            .reason(RefundReason.AMEND)
            .build())
        .booking(Booking
            .builder()
            .channel("WEB")
            .type("PAY_NOW")
            .journey("REFUND")
            .businessSite(BusinessSite
                .builder()
                .identifier("LONEUS")
                .type("HOTEL")
                .build())
            .build())
        .build();
  }

  private RefundResponseDto mockRefundResponseDto() {
    var refundResponseDto = new RefundResponseDto();
    refundResponseDto.setRefundId("refundId123");
    refundResponseDto.setRequestId("7b7ba4e5-d43c-4426-b6aa-ba5d6e0c01ca");
    refundResponseDto.setRefunded(true);
    return refundResponseDto;
  }

  private RefundResponse mockRefundResponse() {
    var refundResponse = new RefundResponse();
    refundResponse.setRefundId("refundId123");
    refundResponse.setRequestId("7b7ba4e5-d43c-4426-b6aa-ba5d6e0c01ca");
    refundResponse.setRefunded(true);
    return refundResponse;
  }


}
