package uk.co.whitbread.basket.infrastructure.queue.processor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.OPEN;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PAY_PENDING;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.PROCESSING;
import static uk.co.whitbread.basket.domain.model.payments.out.BasketRequestAction.COMMIT;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.exception.PaymentFraudException;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.content.out.AcceptedCreditCard;
import uk.co.whitbread.basket.domain.model.content.out.HotelPaymentInformation;
import uk.co.whitbread.basket.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.model.payments.out.Billing;
import uk.co.whitbread.basket.domain.model.payments.out.Booking;
import uk.co.whitbread.basket.domain.model.payments.out.Payment;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ProviderResponse;
import uk.co.whitbread.basket.domain.model.payments.out.ThreeCResponse;
import uk.co.whitbread.basket.domain.ports.secondary.BookingConfirmationDetailsConverterPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.basket.infrastructure.queue.model.PaymentAuthorisedEvent;
import uk.co.whitbread.basket.infrastructure.queue.producer.BasketOrderProducer;

@ExtendWith(MockitoExtension.class)
class PaymentAuthorisedEventProcessorTest {

  @Mock
  private BasketOrderProducer basketOrderProducer;
  @Mock
  private BasketOutPort basketOutPort;
  @Mock
  private PaymentAuthorisedEventPaymentResponseMapper paymentAuthorisedEventPaymentResponseMapper;
  @Mock
  private BookingConfirmationDetailsConverterPort bookingConfirmationDetailsConverterPort;
  @Mock
  private ContentOutPort contentOutPort;
  @Mock
  private UnleashWrapper unleashWrapper;

  @InjectMocks
  private PaymentAuthorisedEventProcessor paymentAuthorisedEventProcessor;

  @Test
  void shouldFailWhenEventIsMissing() {
    assertThrows(IllegalArgumentException.class, () -> paymentAuthorisedEventProcessor.process(null));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldFailWhenBasketReferenceIsMissing() {
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();

    assertThrows(IllegalArgumentException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldFailWhenPaymentsConfirmationIsMissing() {
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(null)
        .build();

    assertThrows(IllegalArgumentException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldFailWhenPaymentIdIsMissing() {
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("").build())
        .build();

    assertThrows(IllegalArgumentException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldPublishCommitForBookingFlow() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .hotelId("123")
        .channel("PI")
        .status(PAY_PENDING)
        .build();
    var paymentsConfirmation = PaymentsConfirmation.builder()
        .paymentId("payment-id")
        .ccAgentId("agent-1")
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(paymentsConfirmation)
        .paymentProvider("THREEC")
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());
    when(contentOutPort.getHotelPaymentDetails(any(), any(), any()))
        .thenReturn(hotelPaymentInformation());
    when(bookingConfirmationDetailsConverterPort.createConfirmationDetails(any(), any(), any(), any()))
        .thenReturn(mock(BookingConfirmationDetails.class));

    paymentAuthorisedEventProcessor.process(event);

    verify(basketOrderProducer, times(1))
        .sendOrderAsync(any(Basket.class), eq(COMMIT.getReqAction()), any(), eq("agent-1"));
    verify(basketOutPort, times(1))
        .updateBasketStatus("basket-id", PROCESSING, Optional.empty());
  }

  @Test
  void shouldPublishAmendForAmendFlow() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .originalBasketId("original-basket-id")
        .status(PAY_PENDING)
        .build();
    var paymentsConfirmation = PaymentsConfirmation.builder()
        .paymentId("payment-id")
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(paymentsConfirmation)
        .paymentProvider("THREEC")
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());

    paymentAuthorisedEventProcessor.process(event);

    verify(basketOrderProducer, times(1))
        .sendAmendAsync(any(Basket.class), any(PaymentsConfirmation.class), any());
    verify(basketOutPort, times(1))
        .updateBasketStatus("basket-id", uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.AMENDING, Optional.empty());
  }

  @Test
  void shouldContinueProcessingWhenReferenceMismatchValidationIsNotApplied() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .channel("PI")
        .status(PAY_PENDING)
        .build();
    var paymentsConfirmation = PaymentsConfirmation.builder()
        .paymentId("payment-id")
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(paymentsConfirmation)
        .build();
    var mismatchResponse = successPaymentResponse();
    mismatchResponse.getBooking().setReference("different-reference");

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(mismatchResponse);

    assertDoesNotThrow(() -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, times(1)).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldThrowPaymentExceptionWhenUnsupportedCardType() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(PAY_PENDING)
        .build();
    var paymentsConfirmation = PaymentsConfirmation.builder()
        .paymentId("payment-id")
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(paymentsConfirmation)
        .build();
    var failedResponse = successPaymentResponse();
    failedResponse.getProviderResponse().getThreecResponse().setCardSchemeId("UNSUPPORTED");

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(failedResponse);
    when(contentOutPort.getHotelPaymentDetails(any(), any(), any()))
        .thenReturn(hotelPaymentInformation());
    when(bookingConfirmationDetailsConverterPort.createConfirmationDetails(any(), any(), any(), any()))
        .thenThrow(new PaymentException(ErrorCode.DIGITAL_UNSUPPORTED_CARD_TYPE_EXCEPTION, "Unsupported cardType=UNSUPPORTED"));

    assertThrows(PaymentException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldThrowPaymentFraudExceptionWhenFailureAndFraudDecisionIsNotAccept() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();
    var failureResponse = successPaymentResponse();
    failureResponse.setPaymentStatus("FAILURE");
    failureResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("REJECT");

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(failureResponse);

    assertThrows(PaymentFraudException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldThrowPaymentExceptionWhenFailureAndFraudDecisionIsAccept() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();
    var failureResponse = successPaymentResponse();
    failureResponse.setPaymentStatus("FAILURE");
    failureResponse.getProviderResponse().getThreecResponse().setFraudCheckDecision("ACCEPT");
    failureResponse.getProviderResponse().getThreecResponse().setProviderReason("DECLINED");

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(failureResponse);

    assertThrows(PaymentException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldNotThrowWhenStatusIsSuccess() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .hotelId("123")
        .channel("PI")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").ccAgentId("agent-1").build())
        .paymentProvider("THREEC")
        .build();
    var successResponse = successPaymentResponse();
    successResponse.setPaymentStatus("SUCCESS");

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successResponse);
    when(contentOutPort.getHotelPaymentDetails(any(), any(), any()))
        .thenReturn(hotelPaymentInformation());
    when(bookingConfirmationDetailsConverterPort.createConfirmationDetails(any(), any(), any(), any()))
        .thenReturn(mock(BookingConfirmationDetails.class));

    assertDoesNotThrow(() -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, times(1))
        .sendOrderAsync(any(Basket.class), eq(COMMIT.getReqAction()), any(), eq("agent-1"));
  }

  @Test
  void shouldDoNothingWhenBasketStatusIsNotPayPending() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(PROCESSING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());

    paymentAuthorisedEventProcessor.process(event);

    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
    verify(basketOrderProducer, never()).sendAmendAsync(any(), any(), any());
  }

  private PaymentResponse successPaymentResponse() {
    String firstName = Base64.getEncoder().encodeToString("John".getBytes(UTF_8));
    String lastName = Base64.getEncoder().encodeToString("Doe".getBytes(UTF_8));
    return PaymentResponse.builder()
        .paymentId("payment-id")
        .paymentStatus("AUTHORIZED")
        .booking(Booking.builder().reference("basket-id").language("en").build())
        .payment(Payment.builder().billing(Billing.builder()
            .firstName(firstName).lastName(lastName).build()).build())
        .providerResponse(ProviderResponse.builder()
            .threecResponse(ThreeCResponse.builder()
                .cardSchemeId("VD")
                .token("tok_123")
                .last4Digits("1111")
                .expiry("12/30")
                .threeDSIndicator("Y")
                .build())
            .build())
        .build();
  }

  private HotelPaymentInformation hotelPaymentInformation() {
    return HotelPaymentInformation.builder()
        .acceptedCreditCards(List.of(AcceptedCreditCard.builder()
            .code3CP("VD")
            .codeOpera("VI")
            .codeOperaCardType("VISA")
            .build()))
        .paymentMethodsOpera(Map.of("PI_VI", "VI"))
        .build();
  }

  @Test
  void shouldSkipProcessingWhenBasketStatusIsAmending() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(AMENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());

    paymentAuthorisedEventProcessor.process(event);

    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
    verify(basketOrderProducer, never()).sendAmendAsync(any(), any(), any());
  }

  @Test
  void shouldLogErrorAndSkipWhenBasketStatusIsUnexpected() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(OPEN)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());

    assertDoesNotThrow(() -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
    verify(basketOrderProducer, never()).sendAmendAsync(any(), any(), any());
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenPaymentStatusIsNotAKnownEnumValue() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .build();
    var unknownStatusResponse = successPaymentResponse();
    unknownStatusResponse.setPaymentStatus("UNKNOWN_STATUS");

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(unknownStatusResponse);

    assertThrows(IllegalArgumentException.class, () -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, never()).sendOrderAsync(any(), any(), any(), any());
  }

  @Test
  void shouldUseEmptyStringForCcAgentIdWhenNullInNewBookingFlow() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .hotelId("123")
        .channel("PI")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").ccAgentId(null).build())
        .paymentProvider("THREEC")
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());
    when(contentOutPort.getHotelPaymentDetails(any(), any(), any()))
        .thenReturn(hotelPaymentInformation());
    when(bookingConfirmationDetailsConverterPort.createConfirmationDetails(any(), any(), any(), any()))
        .thenReturn(mock(BookingConfirmationDetails.class));

    paymentAuthorisedEventProcessor.process(event);

    verify(basketOrderProducer, times(1))
        .sendOrderAsync(any(Basket.class), eq(COMMIT.getReqAction()), any(), eq(""));
  }

  @Test
  void shouldSwallowExceptionWhenUpdateBasketStatusToProcessingFails() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .hotelId("123")
        .channel("PI")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").ccAgentId("agent-1").build())
        .paymentProvider("THREEC")
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());
    when(contentOutPort.getHotelPaymentDetails(any(), any(), any()))
        .thenReturn(hotelPaymentInformation());
    when(bookingConfirmationDetailsConverterPort.createConfirmationDetails(any(), any(), any(), any()))
        .thenReturn(mock(BookingConfirmationDetails.class));
    doThrow(new RuntimeException("DynamoDB unavailable"))
        .when(basketOutPort).updateBasketStatus("basket-id", PROCESSING, Optional.empty());

    assertDoesNotThrow(() -> paymentAuthorisedEventProcessor.process(event));
    verify(basketOrderProducer, times(1))
        .sendOrderAsync(any(Basket.class), eq(COMMIT.getReqAction()), any(), eq("agent-1"));
  }

  @Test
  void shouldPassDatatransProviderToCreateConfirmationDetails() {
    var basket = Basket.builder()
        .basketId("basket-id")
        .reference("basket-id")
        .hotelId("123")
        .channel("PI")
        .status(PAY_PENDING)
        .build();
    var event = PaymentAuthorisedEvent.builder()
        .basketReference("basket-id")
        .paymentsConfirmation(PaymentsConfirmation.builder().paymentId("payment-id").build())
        .paymentProvider("DATATRANS")
        .build();

    when(basketOutPort.getBasketById("basket-id")).thenReturn(basket);
    when(paymentAuthorisedEventPaymentResponseMapper.toPaymentsResponseModel(any(PaymentsConfirmation.class), any(Basket.class)))
        .thenReturn(successPaymentResponse());
    when(contentOutPort.getHotelPaymentDetails(any(), any(), any()))
        .thenReturn(hotelPaymentInformation());
    when(bookingConfirmationDetailsConverterPort.createConfirmationDetails(any(), any(), any(), any()))
        .thenReturn(mock(BookingConfirmationDetails.class));

    paymentAuthorisedEventProcessor.process(event);

    // Verify paymentProvider "DATATRANS" is forwarded to createConfirmationDetails
    verify(bookingConfirmationDetailsConverterPort, times(1))
        .createConfirmationDetails(any(), any(), any(), eq("DATATRANS"));
  }
}
