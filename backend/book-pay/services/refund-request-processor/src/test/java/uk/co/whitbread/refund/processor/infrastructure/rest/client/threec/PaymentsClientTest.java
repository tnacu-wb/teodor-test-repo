package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.refund.processor.domain.model.in.Amount;
import uk.co.whitbread.refund.processor.domain.model.in.Booking;
import uk.co.whitbread.refund.processor.domain.model.in.BusinessSite;
import uk.co.whitbread.refund.processor.domain.model.in.Card;
import uk.co.whitbread.refund.processor.domain.model.in.PaymentType;
import uk.co.whitbread.refund.processor.domain.model.in.Refund;
import uk.co.whitbread.refund.processor.domain.model.in.RefundReason;
import uk.co.whitbread.refund.processor.domain.model.in.TokenRefund;
import uk.co.whitbread.refund.processor.generated.models.payments.PaymentResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.ProviderResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.RefundResponseDto;
import uk.co.whitbread.refund.processor.generated.models.payments.ThreeCResponseDto;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.InvalidPaymentException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.PaymentException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.exception.RefundException;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.properties.ThreecProperties;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PaymentsClientTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private CustomTestResponseSpec responseSpecMock;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private ThreecProperties threecProperties;

    @InjectMocks
    private PaymentsClient paymentsClient;

    @Test
    void sendFullRefund_success() {
        // Arrange
        Mono<Void> emptyResponse = Mono.empty().then();

        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(
                ArgumentMatchers.<Class<Void>>notNull())).thenReturn(emptyResponse);

        // Act
        paymentsClient.sendFullRefund("12345");

        // Assert
        verifyNoMoreInteractions(webClient);
    }

    @Test
    void sendFullRefund_4xx_exception() {
        // Arrange
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

        //Act
        Exception exception = assertThrows(InvalidPaymentException.class, () -> paymentsClient.sendFullRefund("12345"));

        //Assert
        String expectedMessage = "Improper call of Payment Service";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void sendFullRefund_5xx_exception() {
        // Arrange
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

        //Act
        Exception exception = assertThrows(RefundException.class, () -> paymentsClient.sendFullRefund("12345"));

        //Assert
        String expectedMessage = "An error was returned calling the Payment service.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void sendTokenRefund_success() {
        //Arrange
        TokenRefund tokenRefund = mockTokenRefundRequest();

        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RefundResponseDto.class)).thenReturn(mockRefundResponse());

        //Act
        var refundResponseDto = paymentsClient.sendTokenRefund(tokenRefund);

        //Assert
        assertThat(refundResponseDto, notNullValue());
        assertEquals(refundResponseDto.getRequestId(), tokenRefund.getRequestId());
        assertTrue(refundResponseDto.getRefunded());
    }

    @Test
    void sendTokenRefund_4xx_exception() {
        //Arrange
        TokenRefund tokenRefund = mockTokenRefundRequest();

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

        //Act
        Exception exception = assertThrows(InvalidPaymentException.class, () -> paymentsClient.sendTokenRefund(tokenRefund));

        //Assert
        String expectedMessage = "Improper call of Payment Service.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void sendTokenRefund_5xx_exception() {
        //Arrange
        TokenRefund tokenRefund = mockTokenRefundRequest();
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

        //Act
        Exception exception = assertThrows(RefundException.class, () -> paymentsClient.sendTokenRefund(tokenRefund));

        //Assert
        String expectedMessage = "An error was returned calling the Payment service";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getPaymentResponse__ShouldReturnOK() {
        //Arrange
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PaymentResponseDto.class)).thenReturn(mockPaymentResponse());

        //Act
        var paymentResponse = paymentsClient.getPaymentResponse("1234");

        //Assert
        assertThat(paymentResponse, notNullValue());
    }

    @Test
    void getPaymentResponse_4xx_exception() {
        // Arrange
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

        //Act
        Exception exception = assertThrows(InvalidPaymentException.class, () -> paymentsClient.getPaymentResponse("12345"));

        //Assert
        String expectedMessage = "Improper call of Payment Service";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getPaymentResponse_5xx_exception() {
        // Arrange
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

        //Act
        Exception exception = assertThrows(PaymentException.class, () -> paymentsClient.getPaymentResponse("12345"));

        //Assert
        String expectedMessage = "An error was returned calling the Payment service";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private ThreeCResponseDto mockThreecResponse() {
        ThreeCResponseDto threeCResponseDto = new ThreeCResponseDto();
        threeCResponseDto.setTemplate("wb_newcard_pn_v3.xml");
        threeCResponseDto.setiPageHtml(
                "PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFu");
        return threeCResponseDto;
    }

    private Mono<PaymentResponseDto> mockPaymentResponse() {
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        ProviderResponseDto providerResponse = new ProviderResponseDto();
        providerResponse.setThreecResponse(mockThreecResponse());
        paymentResponseDto.setProviderResponse(providerResponse);
        paymentResponseDto.setPaymentId("04230602742D");
        return Mono.just(paymentResponseDto);
    }

    private Mono<RefundResponseDto> mockRefundResponse() {
        RefundResponseDto refundResponseDto = new RefundResponseDto();
        refundResponseDto.setRefundId("refundId123");
        refundResponseDto.setRequestId("7b7ba4e5-d43c-4426-b6aa-ba5d6e0c01ca");
        refundResponseDto.setRefunded(true);
        return Mono.just(refundResponseDto);
    }

    private TokenRefund mockTokenRefundRequest() {
        return TokenRefund
                .builder()
                .requestId("7b7ba4e5-d43c-4426-b6aa-ba5d6e0c01ca")
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

    @Test
    @Tag("opera")
    void sendFullRefundOpera_success() {
        // Arrange
        Mono<Void> emptyResponse = Mono.empty().then();

        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(), any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(
                ArgumentMatchers.<Class<Void>>notNull())).thenReturn(emptyResponse);

        // Act
        paymentsClient.sendFullRefund("12345");

        // Assert
        verifyNoMoreInteractions(webClient);
    }

    @Test
    @Tag("opera")
    void sendTokenRefundOpera_success() {
        //Arrange
        TokenRefund tokenRefund = mockTokenRefundRequest();

        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(RefundResponseDto.class)).thenReturn(mockRefundResponse());

        //Act
        var refundResponseDto = paymentsClient.sendTokenRefund(tokenRefund);

        //Assert
        assertThat(refundResponseDto, notNullValue());
        assertEquals(refundResponseDto.getRequestId(), tokenRefund.getRequestId());
        assertTrue(refundResponseDto.getRefunded());
    }

    @Tag("opera")
    @Test
    void getPaymentResponseOpera_ShouldReturnOK() {
        //Arrange
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PaymentResponseDto.class)).thenReturn(mockPaymentResponse());

        //Act
        var paymentResponse = paymentsClient.getPaymentResponse("1234");

        //Assert
        assertThat(paymentResponse, notNullValue());
    }

}
