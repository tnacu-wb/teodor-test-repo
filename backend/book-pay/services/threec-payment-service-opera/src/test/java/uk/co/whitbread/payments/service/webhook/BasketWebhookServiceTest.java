package uk.co.whitbread.payments.service.webhook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.BasketClient;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.BookingServiceException;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.booking.basket.BasketRequest;
import uk.co.whitbread.payments.model.booking.basket.BasketResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.util.mapper.BasketRequestBuilder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketWebhookServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RevisedSolutionConfig revisedSolutionConfig;

    @Mock
    private BasketClient basketClient;

    @Mock
    private DefaultPaymentService defaultPaymentService;

    private BasketWebhookService basketWebhookService;

    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    @BeforeEach
    void init() {
        basketWebhookService = new BasketWebhookService(basketClient, defaultPaymentService, revisedSolutionConfig,
            new BasketRequestBuilder(), paymentRepository, unleashWrapper);
    }

    @Test
    void testWebHookNotMigratedHotelBooking() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .booking(Booking.builder()
                        .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                        .bookingReference("12345")
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
                        .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                        .bookingReference("12345")
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build())));

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(false);

        var actualResponse = basketWebhookService.makeBooking(paymentId, "1").block();
        assertNull(actualResponse);
        verifyNoInteractions(basketClient);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testWebHookMigratedHotelBooking(Boolean mockedFeatureMockPaymentReturnCodeFf) {
        var paymentId = UUID.randomUUID().toString();
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        var mockedFeatureMockPaymentReturnCode = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMockPaymentReturnCode()).thenReturn(mockedFeatureMockPaymentReturnCode);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMockPaymentReturnCode())).thenReturn(mockedFeatureMockPaymentReturnCodeFf);

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .payment(Payment.builder()
                        .environment("https://www.qaorange.premierinn.digitial")
                        .billing(Billing.builder().firstName("John").build())
                        .build())
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                        .bookingReference("12345")
                        .build())
                .paymentId(paymentId)
                .sessionId("test_sessionId")
                .build();
        when(paymentRepository.getByPaymentId(paymentId)).thenReturn(Mono.just(Optional.of(PaymentsSchema.builder()
                .paymentId(paymentId)
                .payment(Payment.builder()
                        .environment("https://www.qaorange.premierinn.digitial")
                        .billing(Billing.builder().firstName("John").build())
                        .build())
                .sessionId("test_sessionId")
                .booking(Booking.builder()
                        .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                        .bookingReference("12345")
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build())));

        var response = BasketResponse.builder()
                .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                .build();

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);
        when(basketClient.makeBooking(any(BasketRequest.class))).thenReturn(Mono.just(response));

        var actualResponse = basketWebhookService.makeBooking(paymentId, "1").block();
        assertNotNull(actualResponse);
        assertThat(response.getReference()).isEqualTo("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testRefundProcessedWhenBookingServiceErrors(Boolean mockedFeatureMockPaymentReturnCodeFf) {
        var paymentId = UUID.randomUUID().toString();
        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        var mockedFeatureMockReturnCode = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMockPaymentReturnCode()).thenReturn(mockedFeatureMockReturnCode);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMockPaymentReturnCode())).thenReturn(mockedFeatureMockPaymentReturnCodeFf);

        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .payment(Payment.builder()
                        .environment("https://www.uat.premierinn.digitial")
                        .billing(Billing.builder().firstName("John").build())
                        .build())
                .booking(Booking.builder()
                        .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                        .bookingReference("12345")
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
                .payment(Payment.builder()
                        .environment("https://www.uat.premierinn.digitial")
                        .billing(Billing.builder().firstName("John").build())
                        .build())
                .sessionId("test_sessionId")
                .providerResponse(ProviderResponse.builder()
                        .threeCResponse(ThreeCResponse.builder()
                                .providerStatus("CQ").build()).build())
                .booking(Booking.builder()
                        .reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                        .bookingReference("12345")
                        .businessSite(BusinessSite.builder()
                                .identifier("CARNOR")
                                .build())
                        .build())
                .build())));
        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);
        when(basketClient.makeBooking(any(BasketRequest.class))).thenReturn(Mono.error(new BookingServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Connection error", ErrorCodes.CONNECTION_ERROR)));

        when(defaultPaymentService.refund(paymentId)).thenReturn(Mono.empty());

        basketWebhookService.makeBooking(paymentId, "1").block();

        verify(defaultPaymentService, times(1)).refund(paymentId);
    }
}
