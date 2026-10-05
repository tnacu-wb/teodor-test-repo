package uk.co.whitbread.payments.service.webhook;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.HotelBookingClient;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.BookingServiceException;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.booking.BookingStatus;
import uk.co.whitbread.payments.model.booking.WebhookBookingRequest;
import uk.co.whitbread.payments.model.booking.WebhookBookingResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.util.mapper.HotelBookingRequestBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class HotelBookingWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RevisedSolutionConfig revisedSolutionConfig;

    @Mock
    private HotelBookingClient hotelBookingClient;

    @Mock
    private DefaultPaymentService defaultPaymentService;

    @Mock
    private HotelBookingRequestBuilder hotelBookingRequestBuilder;

    @InjectMocks
    private HotelBookingWebhookService hotelBookingWebhookService;

    @Test
    void testWebHookNotMigratedHotelBooking() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .build();
        when(paymentRepository.getByPaymentId(paymentId)).thenReturn(Mono.just(Optional.of(PaymentsSchema.builder()
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build())));

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(false);

        var actualResponse = hotelBookingWebhookService.makeBooking(paymentId).block();
        assertNull(actualResponse);
        verifyNoInteractions(hotelBookingClient);
    }

    @Test
    void testWebHookMigratedHotelBooking() {
        var paymentId = UUID.randomUUID().toString();
        WebhookBookingRequest webhookBookingRequest = WebhookBookingRequest.builder().paymentId(paymentId).build();
        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .build();
        when(paymentRepository.getByPaymentId(paymentId)).thenReturn(Mono.just(Optional.of(PaymentsSchema.builder()
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build())));

        PaymentResponse paymentResponse = PaymentResponse.builder().paymentId(paymentId).build();

        when(defaultPaymentService.createPaymentResponse(paymentId,paymentsSchema)).thenReturn(paymentResponse);
        when(hotelBookingRequestBuilder.buildHotelBookingRequst(paymentResponse)).thenReturn(webhookBookingRequest);

        WebhookBookingResponse response = WebhookBookingResponse.builder()
                .sessionId("test_sessionId")
                .bookingStatus(BookingStatus.COMPLETE)
                .build();

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);
        when(hotelBookingClient.makeBooking(webhookBookingRequest,"test_sessionId")).thenReturn(Mono.just(response));

        var actualResponse = hotelBookingWebhookService.makeBooking(paymentId).block();
        assertNotNull(actualResponse);
        assertThat(response.getSessionId()).isEqualTo(actualResponse.getSessionId());
    }

    @Test
    void testRefundProcessedWhenBookingServiceErrors() {
        var paymentId = UUID.randomUUID().toString();
        WebhookBookingRequest webhookBookingRequest = WebhookBookingRequest.builder().paymentId(paymentId).build();
        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .providerResponse(ProviderResponse.builder()
                        .threeCResponse(ThreeCResponse.builder()
                                .providerStatus("CQ").build()).build())
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .build();
        when(paymentRepository.getByPaymentId(paymentId)).thenReturn(Mono.just(Optional.of(PaymentsSchema.builder()
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .providerResponse(ProviderResponse.builder()
                        .threeCResponse(ThreeCResponse.builder()
                                .providerStatus("CQ").build()).build())
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build())));

        PaymentResponse paymentResponse = PaymentResponse.builder().paymentId(paymentId).build();

        when(defaultPaymentService.createPaymentResponse(paymentId,paymentsSchema)).thenReturn(paymentResponse);
        when(hotelBookingRequestBuilder.buildHotelBookingRequst(paymentResponse)).thenReturn(webhookBookingRequest);

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);
        when(hotelBookingClient.makeBooking(webhookBookingRequest,"test_sessionId")).thenReturn(Mono.error(new BookingServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Connection error", ErrorCodes.CONNECTION_ERROR)));

        when(defaultPaymentService.refund(paymentId)).thenReturn(Mono.empty());

        var actualResponse = hotelBookingWebhookService.makeBooking(paymentId).block();

        verify(defaultPaymentService, times(1)).refund(paymentId);
    }
}
