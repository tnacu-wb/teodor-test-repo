package uk.co.whitbread.refund.processor.domain.logic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.refund.processor.domain.model.in.Amount;
import uk.co.whitbread.refund.processor.domain.model.in.Booking;
import uk.co.whitbread.refund.processor.domain.model.in.BusinessSite;
import uk.co.whitbread.refund.processor.domain.model.in.Card;
import uk.co.whitbread.refund.processor.domain.model.in.PaymentType;
import uk.co.whitbread.refund.processor.domain.model.in.Refund;
import uk.co.whitbread.refund.processor.domain.model.in.RefundReason;
import uk.co.whitbread.refund.processor.domain.model.in.RefundRequest;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.domain.model.out.PaymentResponse;
import uk.co.whitbread.refund.processor.domain.model.out.RefundResponse;
import uk.co.whitbread.refund.processor.infrastructure.queue.BasketAcknowledgeOutPortImpl;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.ErrorCode;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.RefundException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.RefundRequestProcessOutPortImpl;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RefundRequestProcessInPortImplTest {

  @InjectMocks
  private RefundRequestProcessInPortImpl refundRequestProcessInPort;

  @Mock
  private RefundRequestProcessOutPortImpl refundRequestProcessOutPort;

  @Mock
  private BasketAcknowledgeOutPortImpl basketAcknowledgeOutPort;

  @Test
  void testFullRefundProcess_success() {
    //Arrange
    var refundReq = mockRefundRequest();

    when(refundRequestProcessOutPort.checkPaymentResponse(refundReq.getPaymentId())).thenReturn(Optional.of(mockPaymentResponse()));
    doNothing().when(refundRequestProcessOutPort).sendFullRefund(refundReq.getPaymentId());
    when(refundRequestProcessOutPort.getRefundResponse(refundReq.getPaymentId())).thenReturn(mockRefundResponse());

    //Act

    refundRequestProcessInPort.processRefund(refundReq);

    // Assert
    verify(refundRequestProcessOutPort).sendFullRefund(refundReq.getPaymentId());
    verify(refundRequestProcessOutPort).getRefundResponse(refundReq.getPaymentId());
    verify(basketAcknowledgeOutPort).sendAcknowledgeMessage(anyString(), anyString(), anyString(), anyBoolean());
  }

  @Test
  void testFullRefundWithTokenRefundProcess_success() {
    //Arrange
    var refundReq = mockRefundRequest();

    when(refundRequestProcessOutPort.checkPaymentResponse(refundReq.getPaymentId())).thenReturn(Optional.empty());
    when(refundRequestProcessOutPort.sendTokenRefund(any())).thenReturn(mockRefundResponse());

    //Act

    refundRequestProcessInPort.processRefund(refundReq);

    // Assert
    verify(refundRequestProcessOutPort).sendTokenRefund(any());
    verify(basketAcknowledgeOutPort).sendAcknowledgeMessage(anyString(), anyString(), anyString(), anyBoolean());
  }

  @Test
  void testPartialRefundProcess_success() {
    //Arrange
    var refundReq = mockTokenRefund();

    when(refundRequestProcessOutPort.sendTokenRefund(refundReq)).thenReturn(mockRefundResponse());

    //Act
    refundRequestProcessInPort.handleTokenRefund(refundReq);

    // Assert
    verify(refundRequestProcessOutPort).sendTokenRefund(refundReq);
  }

  @Test
  void testHandleFailedRefund_success() {
    //Arrange
    doNothing().when(basketAcknowledgeOutPort)
        .sendAcknowledgeMessage(any(String.class), any(String.class), any(String.class), eq(false));
    var refundReq = mockRefundRequest();

    //Act
    refundRequestProcessInPort.handleFailedRefund(refundReq);

    // Assert
    verify(basketAcknowledgeOutPort)
        .sendAcknowledgeMessage(refundReq.getBasketReference(), refundReq.getItemId(), "AMEND",false);
  }

  @Test
  void sendTokenRefund_exception() {
    //Arrange
    var refundReq = mockTokenRefund();
    when(refundRequestProcessOutPort.sendTokenRefund(refundReq)).thenThrow(new RefundException(
        ErrorCode.TOKEN_REFUND_EXCEPTION, "An error was returned calling the Payment service."));


    //Act
    Exception exception = assertThrows(RefundException.class, () -> refundRequestProcessInPort.handleTokenRefund(refundReq));

    //Assert
    String expectedMessage = "An error was returned calling the Payment service.";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  private RefundResponse mockRefundResponse() {
    return new RefundResponse("abcd123", "abc1222", "123", true);
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

  private RefundRequest mockRefundRequest() {
    return RefundRequest
            .builder()
            .itemId("ABCD#12345")
            .paymentId("123456789")
            .hotelCode("LONEUS")
            .basketReference("AKV123466")
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

  private PaymentResponse mockPaymentResponse() {
    PaymentResponse paymentResponse = new PaymentResponse();
    paymentResponse.setPaymentId("123456789");
    paymentResponse.setRefunded(true);
    return paymentResponse;
  }

}
