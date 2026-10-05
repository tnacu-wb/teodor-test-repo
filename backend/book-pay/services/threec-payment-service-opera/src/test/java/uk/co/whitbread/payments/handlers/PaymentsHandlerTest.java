package uk.co.whitbread.payments.handlers;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.PaymentSubType;
import uk.co.whitbread.payments.service.impl.DefaultPaymentWebhookService;
import uk.co.whitbread.payments.service.webhook.BasketWebhookService;
import uk.co.whitbread.payments.service.webhook.HotelBookingWebhookService;
import uk.co.whitbread.payments.service.webhook.HotelCardWebhookService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsHandlerTest {

    @Mock
    private DefaultPaymentWebhookService paymentWebhookService;
    @Mock
    private HotelBookingWebhookService hotelBookingWebhookService;
    @Mock
    private BasketWebhookService basketWebhookService;
    @Mock
    private HotelCardWebhookService hotelCardWebhookService;
    @Mock
    private RevisedSolutionConfig revisedSolutionConfig;

    @InjectMocks
    private PaymentsHandler paymentsHandler;

    @ParameterizedTest
    @ValueSource(strings = {""})
    void isBartHotelTest(String bookingReference) {

        var result = paymentsHandler.isOpera.test(bookingReference);

        assertThat(result).isFalse();
    }

   @Test
    void isBartHotelWithNullTest() {

        var result = paymentsHandler.isOpera.test(null);

        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1114", "lkir", "lkitt5647282-198654", "---t99088-3348", "jhf22-123"})
    void isOperaHotelTest(String bookingReference) {

        var result = paymentsHandler.isOpera.test(bookingReference);

        assertThat(result).isTrue();
    }

    @Test
    void webhookToOperaTest() {

        var request = mockServerRequest();
        when(paymentWebhookService.handleWebhook(request)).thenReturn(Mono.just(PaymentResponse.builder()
                        .booking(Booking.builder()
                                .reference("12345-4325-asdfsra")
                                .build())
                .build()));
        when(revisedSolutionConfig.isFeatureEnabled()).thenReturn(true);

        paymentsHandler.webhook(request).subscribe();

        verifyNoInteractions(hotelBookingWebhookService);
        verifyNoInteractions(hotelCardWebhookService);
        verify(basketWebhookService, times(1)).makeBooking(anyString(), anyString());
    }

    @Test
    void webhookToBartTest() {
        var request = mockServerRequest();
        when(paymentWebhookService.handleWebhook(request)).thenReturn(Mono.just(PaymentResponse.builder()
                .bookingReference(null)
                .build()));
        when(revisedSolutionConfig.isFeatureEnabled()).thenReturn(true);

        paymentsHandler.webhook(request).subscribe();

        verifyNoInteractions(basketWebhookService);
        verifyNoInteractions(hotelCardWebhookService);
        verify(hotelBookingWebhookService, times(1)).makeBooking(anyString());
    }

  @Test
  void webhookToHotelCardTest() {

    var request = mockServerRequest();
    when(paymentWebhookService.handleWebhook(request)).thenReturn(Mono.just(PaymentResponse.builder()
        .payment(Payment.builder().subType("SAVE_CARD").build())
        .build()));

    paymentsHandler.webhook(request).subscribe();

    verifyNoInteractions(hotelBookingWebhookService);
    verifyNoInteractions(basketWebhookService);
    verify(hotelCardWebhookService, times(1)).saveOrUpdateCard(any());
  }

  @Test
  void webhookNoInteractionsHotelCardTest() {

    var request = mockServerRequest();
    when(paymentWebhookService.handleWebhook(request)).thenReturn(Mono.just(PaymentResponse.builder()
        .payment(Payment.builder().subType("PIBADE").build())
        .build()));
    when(revisedSolutionConfig.isFeatureEnabled()).thenReturn(true);

    paymentsHandler.webhook(request).subscribe();

    verifyNoInteractions(hotelCardWebhookService);
  }

  @Test
  void webhookIsNotRevisedSolutionTest() {

    var request = mockServerRequest();
    when(paymentWebhookService.handleWebhook(request)).thenReturn(Mono.just(PaymentResponse.builder().build()));
    when(revisedSolutionConfig.isFeatureEnabled()).thenReturn(false);

    paymentsHandler.webhook(request).subscribe();

    verifyNoInteractions(hotelBookingWebhookService);
    verifyNoInteractions(basketWebhookService);
    verifyNoInteractions(hotelCardWebhookService);
  }

    @Test
    void webhookToAuthorizeScaTest() {
        var request = mockServerRequest();
        when(paymentWebhookService.handleWebhook(request)).thenReturn(Mono.just(PaymentResponse.builder()
                .payment(Payment.builder().subType(PaymentSubType.AUTHORIZE_CARD.name()).build())
                .build()));

        paymentsHandler.webhook(request).subscribe();

        verifyNoInteractions(hotelBookingWebhookService);
        verifyNoInteractions(basketWebhookService);
        verifyNoInteractions(hotelCardWebhookService);
    }

  @NotNull
  private static MockServerRequest mockServerRequest() {
    return MockServerRequest.builder()
        .pathVariable("paymentId", "100D")
        .build();
  }
}
