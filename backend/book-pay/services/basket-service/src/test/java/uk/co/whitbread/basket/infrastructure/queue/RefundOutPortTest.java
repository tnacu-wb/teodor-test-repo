package uk.co.whitbread.basket.infrastructure.queue;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentInvalidException;
import uk.co.whitbread.basket.domain.model.payments.out.RefundResponse;
import uk.co.whitbread.basket.domain.model.refund.in.Amount;
import uk.co.whitbread.basket.domain.model.refund.in.Booking;
import uk.co.whitbread.basket.domain.model.refund.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.refund.in.Card;
import uk.co.whitbread.basket.domain.model.refund.in.PaymentType;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundType;
import uk.co.whitbread.basket.generated.models.payments.AmountDto;
import uk.co.whitbread.basket.generated.models.payments.BookingDto;
import uk.co.whitbread.basket.generated.models.payments.BusinessSiteDto;
import uk.co.whitbread.basket.generated.models.payments.CardDto;
import uk.co.whitbread.basket.generated.models.payments.RefundDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundRequestDto;
import uk.co.whitbread.basket.generated.models.payments.TokenRefundResponseDto;
import uk.co.whitbread.basket.infrastructure.queue.model.RefundRequestEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.RefundProducer;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentRequestMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.mapper.PaymentResponseMapper;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.PaymentsClient;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class RefundOutPortTest {

    @Mock
    private RefundProducer refundProducer;

    @Mock
    private PaymentsClient paymentsClient;

    @Mock
    private PaymentRequestMapper paymentRequestMapper;

    @Mock
    private PaymentResponseMapper paymentResponseMapper;

    @InjectMocks
    private RefundOutPortImpl refundOutPort;

    @Test
    public void testProcessFullRefund() {
        var basketReference = "BKR12345";
        // Arrange
        doNothing().when(refundProducer).sendRefundRequest(any(RefundRequestEvent.class));

        // Act
        refundOutPort.processRefund(basketReference, mockFullRefundRequest(), anyString());

        // Assert
        verify(refundProducer, times(1)).sendRefundRequest(any(RefundRequestEvent.class));
    }

    @Test
    public void testProcessPartialRefund() {
        // Arrange

        when(paymentRequestMapper.toTokenRefundRequestDto(mockPartialRefundRequest())).thenReturn(getTokenRefundRequest());
        when(paymentsClient.sendPartialRefund(any())).thenReturn(mockRefundResponseDto());
        when(paymentResponseMapper.toRefundModel(any())).thenReturn(mockRefundResponse());
        // Act
        var response = refundOutPort.processTokenRefund(mockPartialRefundRequest());

        // Assert
        assertTrue(response.isPresent());
        verify(paymentsClient, times(1)).sendPartialRefund(any(TokenRefundRequestDto.class));
    }

    @Test
    void testProcessPartialRefund_4xx_exception() {
        //Arrange
        when(paymentRequestMapper.toTokenRefundRequestDto(mockPartialRefundRequest())).thenReturn(getTokenRefundRequest());
        when(paymentsClient.sendPartialRefund(any())).thenThrow(new PaymentInvalidException(
            ErrorCode.TOKEN_REFUND_CLIENT_EXCEPTION, "Improper call of Payment Service"));


        //Act
        Exception exception = assertThrows(PaymentInvalidException.class, () -> refundOutPort.processTokenRefund(mockPartialRefundRequest()));

        //Assert
        String expectedMessage = "Improper call of Payment Service";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testProcessPartialRefund_5xx_exception() {
        //Arrange
        when(paymentRequestMapper.toTokenRefundRequestDto(mockPartialRefundRequest())).thenReturn(getTokenRefundRequest());
        when(paymentsClient.sendPartialRefund(any())).thenThrow(
            new PaymentException(ErrorCode.TOKEN_REFUND_SERVER_EXCEPTION,
                "Payment service failed to process after max retries."));


        //Act
        Exception exception = assertThrows(PaymentException.class, () -> refundOutPort.processTokenRefund(mockPartialRefundRequest()));

        //Assert
        String expectedMessage = "Payment service failed to process after max retries.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private RefundRequest mockFullRefundRequest() {
        return RefundRequest
                .builder()
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
                .refundType(RefundType.FULL)
                .build();
    }

    private RefundRequest mockPartialRefundRequest() {
        return RefundRequest
                .builder()
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
                .refundType(RefundType.PARTIAL)
                .build();
    }

    private TokenRefundRequestDto getTokenRefundRequest() {
        var tokenRefundRequestDto = new TokenRefundRequestDto();

        var amountDto = new AmountDto();
        amountDto.currency(AmountDto.CurrencyEnum.GBP);
        amountDto.minorUnits(10);

        var cardDto = new CardDto();
        cardDto.token("4216333880397891103");
        cardDto.expiryYear("26");
        cardDto.expiryMonth("03");

        var refundDto = new RefundDto();
        refundDto.amount(amountDto);
        refundDto.card(cardDto);
        refundDto.type(RefundDto.TypeEnum.CARD);
        refundDto.reason("Amend");

        var businessSite = new BusinessSiteDto();
        businessSite.identifier("LONEUS");
        businessSite.type(BusinessSiteDto.TypeEnum.HOTEL);

        var bookingDto = new BookingDto();
        bookingDto.channel(BookingDto.ChannelEnum.WEB);
        bookingDto.type(BookingDto.TypeEnum.PAY_NOW);
        bookingDto.journey(BookingDto.JourneyEnum.REFUND);

        tokenRefundRequestDto.requestId("ABCD#12345");
        tokenRefundRequestDto.hotelCode("LONEUS");
        tokenRefundRequestDto.refund(refundDto);
        tokenRefundRequestDto.booking(bookingDto);

        return tokenRefundRequestDto;
    }

    private TokenRefundResponseDto mockRefundResponseDto(){
        var refundResponseDto = new TokenRefundResponseDto();
        refundResponseDto.refundId("123refund");
        refundResponseDto.setRefunded(true);
        refundResponseDto.setPaymentId("123paymentId");
        refundResponseDto.setRequestId("123reqId");
        return refundResponseDto;
    }

    private RefundResponse mockRefundResponse(){
        var refundResponse = new RefundResponse();
        refundResponse.setRefundId("123refund");
        refundResponse.setRefunded(true);
        refundResponse.setPaymentId("123paymentId");
        refundResponse.setRequestId("123reqId");
        return refundResponse;
    }

}
