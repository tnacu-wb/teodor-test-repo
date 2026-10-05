package uk.co.whitbread.payments.service.webhook;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getServerRequest;
import static uk.co.whitbread.payments.util.PaymentRequestFixtures.getWebhookData;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.client.BasketClient;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.integration.IntegrationBaseIT;
import uk.co.whitbread.payments.model.Billing;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Card;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.booking.basket.BasketRequest;
import uk.co.whitbread.payments.model.booking.basket.BasketResponse;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.feature.FeatureFlag;
import uk.co.whitbread.payments.model.feature.UnleashWrapper;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.impl.DefaultPaymentService;
import uk.co.whitbread.payments.service.impl.DefaultPaymentWebhookService;
import uk.co.whitbread.payments.util.mapper.BasketRequestBuilder;

@DirtiesContext
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BasketWebhookServiceTestIT extends IntegrationBaseIT {
    @InjectMocks
    private BasketWebhookService basketWebhookService;
    @Mock
    private DefaultPaymentWebhookService defaultPaymentWebhookService;
    @MockitoBean
    private BasketClient basketClient;
    @Mock
    private DefaultPaymentService defaultPaymentService;
    @Mock
    private RevisedSolutionConfig revisedSolutionConfig;
    @Mock
    private PaymentRepository paymentRepoMock;
    @Mock
    private UnleashWrapper<FeatureFlag> unleashWrapper;

    final String PAYMENT_ID = "1111111111D";

    @BeforeEach
    void init() {
        basketWebhookService = new BasketWebhookService(basketClient, defaultPaymentService, revisedSolutionConfig,
                new BasketRequestBuilder(), paymentRepoMock, unleashWrapper);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void callBasketSuccessTest(Boolean mockedFeatureMockPaymentReturnCodeFf) throws IOException  {
        String requestId = "12345";
        var paymentId = UUID.randomUUID().toString();
        var paymentRequest = PaymentRequest.builder()
                .requestId(requestId)
                .payment(Payment.builder()
                        .subType("ECOMM")
                        .environment("http://localhost")
                        .build())
                .booking(Booking.builder()
                        .businessSite(BusinessSite.builder()
                                .name("London Hotel")
                                .identifier("OTHER")
                                .build())
                        .reference("ref-12345")
                        .bookingReference("book-ref-12345")
                        .channel("WEB")
                        .build())
                .build();

        //Create a payment resource
        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
                .payment(Payment.builder()
                        .card(Card.builder().token("1234565").build())
                        .settlementReference("123456789012345678")
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
                .requestId(requestId)
                .build();
        //Create a payment resource
        Optional<PaymentsSchema> paymentsSchemaOptional = Optional.of(paymentsSchema);
        when(paymentRepoMock.createPaymentResource(paymentRequest, getProviderAccount())).thenReturn(Mono.just(paymentsSchema));

        var serverRequest = getServerRequest(getWebhookData(), paymentId);
        assertThat(serverRequest).isNotNull();
        var handleWebhookResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/operaDataReturnsBookingReferenceSuccess.json"), PaymentResponse.class);
        when(defaultPaymentWebhookService.handleWebhook(serverRequest)).thenReturn(Mono.just(handleWebhookResponse));
        var result = defaultPaymentWebhookService.handleWebhook(serverRequest).block();
        assertThat(result).isNotNull();

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);
        when(paymentRepoMock.getByPaymentId(any())).thenReturn(Mono.just(paymentsSchemaOptional));

        var response = BasketResponse.builder().reference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6abc").build();
        when(basketClient.makeBooking(any(BasketRequest.class))).thenReturn(Mono.just(response));

        var mockedFeatureFlag = mock(FeatureFlag.class);
        when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
        var mockedFeatureMockReturnCode = mock(FeatureFlag.Feature.class);
        when(mockedFeatureFlag.getMockPaymentReturnCode()).thenReturn(mockedFeatureMockReturnCode);
        when(unleashWrapper.isEnabled(mockedFeatureFlag.getMockPaymentReturnCode())).thenReturn(mockedFeatureMockPaymentReturnCodeFf);

        var basketWebhookServiceResult = basketWebhookService.makeBooking(result.getPaymentId(), "1").block();
        assertThat(basketWebhookServiceResult).isNotNull();
        assertThat(basketWebhookServiceResult.getReference()).isEqualTo("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6abc");
    }

    @Test
    void makeBookingHandlesNullPointerException() {
        // Arrange
        PaymentsSchema paymentsSchema = PaymentsSchema.builder()
            .paymentId(PAYMENT_ID)
            .booking(Booking.builder()
                .reference("12345")
                .bookingReference("GAA-97fd30b4-0bef-43a3-ab0c-95c9e2e4d6ab")
                .build())
            .build();
        when(paymentRepoMock.getByPaymentId(PAYMENT_ID)).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);
        when(defaultPaymentService.getPaymentStatus(any())).thenCallRealMethod();
        when(defaultPaymentService.refund(PAYMENT_ID)).thenReturn(Mono.empty());

        // Act
        var result = basketWebhookService.makeBooking(PAYMENT_ID, "1").block();

        // Assert
        verify(defaultPaymentService).refund(PAYMENT_ID);
        assertThat(result).isNull();
    }
    private ProviderAccount getProviderAccount() {
        AccountConfigProperties accountConfigProperties = new AccountConfigProperties();
        accountConfigProperties.setNewCardTemplate("");
        return ProviderAccount.builder().configuration(accountConfigProperties).build();
    }
}
