package uk.co.whitbread.payments.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import uk.co.whitbread.payments.client.ThreeCPaymentClient;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalSuccessCodes;
import uk.co.whitbread.payments.config.RevisedSolutionConfig;
import uk.co.whitbread.payments.exception.ErrorCode;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentProcessingException;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.Billing;
import uk.co.whitbread.payments.model.Booking;
import uk.co.whitbread.payments.model.BusinessSite;
import uk.co.whitbread.payments.model.Payment;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.PaymentStatus;
import uk.co.whitbread.payments.model.ProviderResponse;
import uk.co.whitbread.payments.model.ThreeCResponse;
import uk.co.whitbread.payments.model.UpdateBookingReferenceRequest;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.InitialiseResponse;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponseBody;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdResponse;
import uk.co.whitbread.payments.properties.EckohProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.repository.PaymentRepository;
import uk.co.whitbread.payments.service.ProviderAccountFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultPaymentServiceTest {

    private static final String ANY_PAYMENT_ID = "1234567890D";
    private static final String BOOKING_REFERENCE = "ANY_BOOKING_REFERENCE";
    private static final String TEMPLATE = "wb_newcard_pn_v0.xml";
    private static final String ERROR_MESSAGE = "Payment with paymentId %s not found.";

    private static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).findAndRegisterModules().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    private ThreeCPaymentClient threeCPaymentClient;
    @Mock
    private EckohProperties eckohProperties;
    @Mock
    private HtmlTemplateService htmlTemplateService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ThreeCProperties threeCProperties;
    @Mock
    private ProviderAccountFactory providerAccountFactory;
    @InjectMocks
    private DefaultPaymentService defaultPaymentService;
    @Mock
    private RevisedSolutionConfig revisedSolutionConfig;
    @Mock
    private PaypalConfig paypalConfig;

    static Stream<Arguments> createPaymentRequests() throws IOException {
        return Stream.of(Arguments.of(
            read("createPaymentRequest.json"),
            read("createPaymentRequestWithSavedCard.json"),
            read("createPaymentRequestEurMoto.json"),
            read("createPaymentRequestMotoPayOnArrival.json"),
            read("createPaymentRequestWithSecureBooking.json")
        ));
    }

    private static PaymentRequest read(String fileName) throws IOException {
        return objectMapper.readValue(
            new File("src/test/resources/stubs/3c/requests/" + fileName),
            PaymentRequest.class
        );
    }

    static Stream<Arguments> createPaypalPaymentRequests() throws IOException {
        return Stream.of(Arguments.of(objectMapper.readValue(new File("src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"),PaymentRequest.class)));
    }

    @ParameterizedTest
    @MethodSource("createPaymentRequests")
    void testCreatePayment(PaymentRequest paymentRequest) throws IOException {
        when(providerAccountFactory.getAccount(any())).thenReturn(mock(ProviderAccount.class));
        when(htmlTemplateService.getIPageHtml(any(), any(), any(), any())).thenReturn("iPage template HTML");
        lenient().when(threeCPaymentClient.initialiseIpage(any(PaymentRequest.class), any(), any(), any())).thenReturn(Mono.just(getInitialiseResponse()));
        lenient().when(paymentRepository.createPaymentResource(any(), any()))
                .thenReturn(Mono.just(PaymentsSchema.builder()
                        .providerResponse(null)
                        .paymentId(ANY_PAYMENT_ID)
                        .booking(Booking.builder().businessSite(BusinessSite.builder().identifier("CARNOR").build()).build())
                        .template(TEMPLATE).build()));
        lenient().when(threeCPaymentClient.noCardReadRequest(any(PaymentRequest.class), any(), any())).thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadResponse.json"), NoCardReadTransactionResponse.class)));
        var paymentsResponse = defaultPaymentService.createPayment(paymentRequest).block();
        assertNotNull(paymentsResponse);
        assertNotNull(paymentsResponse.getPaymentId());
        assertNotNull(paymentsResponse.getProviderResponse());
        assertNotNull(paymentsResponse.getProviderResponse().getThreeCResponse().getSessionId());
        assertNull(paymentsResponse.getProviderResponse().getThreeCResponse().getProviderUrl());
        assertNotNull(paymentsResponse.getProviderResponse().getThreeCResponse().getIPageHtml());
        assertEquals(TEMPLATE, paymentsResponse.getProviderResponse().getThreeCResponse().getTemplate());
    }

    @Test
    void testCreateEckohPaymentSuccess() throws IOException {
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/createEckohPaymentRequest.json"), PaymentRequest.class);
        when(providerAccountFactory.getAccount(any())).thenReturn(mock(ProviderAccount.class));
        when(eckohProperties.isEnabled()).thenReturn(true);
        lenient().when(paymentRepository.createPaymentResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder().providerResponse(null).paymentId(ANY_PAYMENT_ID).build()));

        var paymentsResponse = defaultPaymentService.createPayment(paymentRequest).block();

        assertNotNull(paymentsResponse);
        assertNotNull(paymentsResponse.getPaymentId());
        assertEquals(ANY_PAYMENT_ID, paymentsResponse.getPaymentId());
    }

    @Test
    void testCreateEckohPaymentFeatureDisabled() throws IOException {
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/createEckohPaymentRequest.json"), PaymentRequest.class);
        when(providerAccountFactory.getAccount(any())).thenReturn(mock(ProviderAccount.class));
        when(eckohProperties.isEnabled()).thenReturn(false);
        lenient().when(paymentRepository.createPaymentResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder().providerResponse(null).paymentId(ANY_PAYMENT_ID).build()));

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> defaultPaymentService.createPayment(paymentRequest).block());
        assertEquals(ErrorCodes.ERROR_HANDLING_REQUEST, exception.getErrorCode());
    }

    @Test
    void testCreatePaymentIpageInitialiseError() throws IOException {
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        lenient().when(threeCPaymentClient.initialiseIpage(any(PaymentRequest.class), any(), any(), any())).thenReturn(Mono.just(getInitialiseErrorResponse()));
        lenient().when(paymentRepository.createPaymentResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder().providerResponse(null).paymentId(ANY_PAYMENT_ID).build()));
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> defaultPaymentService.createPayment(paymentRequest).block());
        assertEquals(ErrorCodes.PROVIDER_ERROR, exception.getErrorCode());
    }

    @Test
    void testHandleRedirect() {
        var paymentId = UUID.randomUUID().toString();
        var response = "<html>\n" + "<head>\n" + "    <title>Payment Details</title>\n" + "</head>\n" + "<body>\n" + "    <script type=\"text/javascript\">\n" + "        [(${commentStart})]\n" + "        try {\n" + "            parent.postMessage(\"{\\\"paymentId\\\": \\\" " + paymentId + " \\\", \\\"merchantReference\\\": \\\"[[${merchantReference}]]\\\",\\\"transactionId\\\": \\\"[[${transactionId}]]\\\",\\\"authCode\\\": \\\"[[${authCode}]]\\\",\\\"cardType\\\": \\\"[[${cardType}]]\\\", \\\"tokenNo\\\": \\\"[[${tokenNo}]]\\\"}\", \"[(${domainUrl})]\");\n" + "        } catch (error) {\n" + "            if (console) {\n" + "                console.log(error);\n" + "            }\n" + "        }\n" + "        [(${commentEnd})]\n" + "    </script>\n" + "</body>\n" + "</html>";
        when(paymentRepository.getByPaymentId(paymentId)).thenReturn(Mono.just(Optional.of(PaymentsSchema.builder().payment(Payment.builder().environment("https://www.premierinn.com").build()).build())));
        when(htmlTemplateService.getRedirectHtml(any(), any(), any(), any())).thenReturn(response);
        var queryParams = new HashMap<String, String>() {{
            put("MerchantRef", "1234");
            put("TxID", "4321");
        }};
        String html = defaultPaymentService.handleRedirect(queryParams, paymentId).block();
        assertNotNull(html);
        assertTrue(html.contains("merchantReference"));
    }

    @Test
    void testGetPayment() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getTemplate()).thenReturn(TEMPLATE);
        when(threeCResponse.getProviderStatus()).thenReturn("");
        when(threeCResponse.getProviderResult()).thenReturn("");

        when(paymentsSchema.getBookingReference()).thenReturn(BOOKING_REFERENCE);
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        when(paymentsSchema.getPayment()).thenReturn(mock(Payment.class));
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertNotNull(paymentResponse);
        assertEquals(paymentId, paymentResponse.getPaymentId());
        assertEquals(TEMPLATE, paymentResponse.getProviderResponse().getThreeCResponse().getTemplate());
        assertEquals(BOOKING_REFERENCE, paymentResponse.getBookingReference());
    }

    @Test
    void testGetPaymentStatusSuccess() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("CQ");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        when(providerResponse.getThreeCResponse().getProviderResult()).thenReturn("0000");

        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(PaymentStatus.SUCCESS.getStatus(), paymentResponse.getPaymentStatus());
    }

    @Test
    void testGetPaymentStatusPending() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("DE");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        when(providerResponse.getThreeCResponse().getProviderResult()).thenReturn("");

        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(PaymentStatus.PENDING.getStatus(), paymentResponse.getPaymentStatus());
    }

    @Test
    void testGetPaymentStatusFailure() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);
        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("RE");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);

        when(providerResponse.getThreeCResponse().getProviderResult()).thenReturn("0078");
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(PaymentStatus.FAILURE.getStatus(), paymentResponse.getPaymentStatus());
    }

    @Test
    void testGetPaymentStatusNoPaymentAttempt711() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("RE");
        when(threeCResponse.getProviderResult()).thenReturn("711");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(PaymentStatus.NO_PAYMENT_ATTEMPT.getStatus(), paymentResponse.getPaymentStatus());
    }

    @Test
    void testGetPaymentStatusNoPaymentAttempt752() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("RE");
        when(threeCResponse.getProviderResult()).thenReturn("752");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(PaymentStatus.NO_PAYMENT_ATTEMPT.getStatus(), paymentResponse.getPaymentStatus());
    }

    @Test
    void testGetPaymentNotFoundThrowsPaymentException() {
        var paymentId = UUID.randomUUID().toString();
        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.empty()));
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> defaultPaymentService.getPayment(paymentId,"").block());
        assertEquals(ErrorCodes.PAYMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testTransactionalRefund() throws IOException {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentsSchema.getProviderResponse()).thenReturn(mock(ProviderResponse.class));
        when(paymentsSchema.getProviderResponse().getThreeCResponse()).thenReturn(mock(ThreeCResponse.class));
        when(paymentsSchema.getProviderResponse().getThreeCResponse().getProviderStatus()).thenReturn("CQ");
        when(paymentsSchema.isRefunded()).thenReturn(false);
        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(paymentRepository.updatePaymentResourceToRefunded(any())).thenReturn(Mono.just(paymentsSchema));
        when(threeCPaymentClient.refund(any())).thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/reverseByTransactionIdResponse.json"), ReverseByTransactionIdResponse.class)));
        defaultPaymentService.validRefundStatus = List.of("CS", "CQ");
        var paymentResponse = defaultPaymentService.refund(paymentId).block().get();
        assertEquals(paymentId, paymentResponse.getPaymentId());
    }

    @Test
    void testTransactionalRefundSkippedWhenPaymentIsNotInCorrectState() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentsSchema.isRefunded()).thenReturn(false);
        when(paymentsSchema.getProviderResponse()).thenReturn(mock(ProviderResponse.class));
        when(paymentsSchema.getProviderResponse().getThreeCResponse()).thenReturn(mock(ThreeCResponse.class));
        when(paymentsSchema.getProviderResponse().getThreeCResponse().getProviderStatus()).thenReturn("AD");
        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        defaultPaymentService.validRefundStatus = List.of("CS", "CQ");
        var paymentResponse = defaultPaymentService.refund(paymentId).block();
        assertTrue(paymentResponse.isEmpty());
    }

    @Test
    void testGetPaymentStatusWithEckohWebhook() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = ThreeCResponse.builder().providerReason("").providerStatus("").providerResult("").providerUrl("").providerStatusText("").token("").authCode("").expiry("").fraudCheckDecision("").fraudCheckResult("").fraudCheckResultReason("").iPageHtml("").cardSchemeId("").cardholderFirstName("").cardholderLastName("").cardSchemeName("").build();

        when(paymentRepository.getByPaymentId(any())).thenReturn(
                Mono.just(Optional.of(paymentsSchema)));
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");

        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(PaymentStatus.PENDING.getStatus(), paymentResponse.getPaymentStatus());
    }

    @Test
    void testTransactionalRefundAlreadyRefunded() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentsSchema.isRefunded()).thenReturn(true);
        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        var paymentResponse = defaultPaymentService.refund(paymentId).block();
        assertTrue(paymentResponse.isEmpty());
    }

    @Test
    void verifyCardExpiry() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("CardExpiry", "2301");
        String expiryDate = defaultPaymentService.getExpiryDate(objectNode);
        assertEquals("01/23", expiryDate);
    }

    @Test
    void verifyTokenExpiry() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("TokenExpiry", "2301");
        String expiryDate = defaultPaymentService.getExpiryDate(objectNode);
        assertEquals("01/23", expiryDate);
    }

    @Test
    void verifyCardExpiryIsPreferred() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("CardExpiry", "2501");
        objectNode.put("TokenExpiry", "2301");
        String expiryDate = defaultPaymentService.getExpiryDate(objectNode);
        assertEquals("01/25", expiryDate);
    }

    @Test
    void verifyCardExpiryFallback() {
        PaymentProviderTransactionResponse.Params params = new PaymentProviderTransactionResponse.Params();
        params.setExpiry("0431");
        String expiryDate = defaultPaymentService.getExpiryDate(params);
        assertEquals("04/31", expiryDate);
    }

    @Test
    void verifyTokenExpiryFallback() {
        PaymentProviderTransactionResponse.Params params = new PaymentProviderTransactionResponse.Params();
        params.setTokenExpiry("0431");
        String expiryDate = defaultPaymentService.getExpiryDate(params);
        assertEquals("04/31", expiryDate);
    }

    @Test
    void verifyCardExpiryIsPreferredFallback() {
        PaymentProviderTransactionResponse.Params params = new PaymentProviderTransactionResponse.Params();
        params.setExpiry("0431");
        params.setTokenExpiry("2301");
        String expiryDate = defaultPaymentService.getExpiryDate(params);
        assertEquals("04/31", expiryDate);
    }

    @Test
    void verifyNoExpiry() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        String expiryDate = defaultPaymentService.getExpiryDate(objectNode);
        assertEquals("", expiryDate);
    }

    private InitialiseResponse getInitialiseResponse() {
        return InitialiseResponse.builder().ipgSession("Test Session").ipgResultCode(0).ipgResultText("").build();
    }

    private InitialiseResponse getInitialiseErrorResponse() {
        return InitialiseResponse.builder().ipgResultCode(1003).ipgResultText("iPage template whitbread-poa-test.xml cannot be found in cache").build();
    }

    @Test
    void testGetPaymentWithoutProviderResponse() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));

        PaymentProviderTransactionResponse paymentProviderTransactionResponse = mock(PaymentProviderTransactionResponse.class);
        when(threeCPaymentClient.retrievePaymentProviderTransactionResponse(any())).thenReturn(Mono.just(paymentProviderTransactionResponse));

        when(paymentProviderTransactionResponse.getResponse()).thenReturn(mock(PaymentProviderTransactionResponse.Response.class));
        when(paymentProviderTransactionResponse.getResponse().getParams()).thenReturn(mock(PaymentProviderTransactionResponse.Params.class));
        when(paymentProviderTransactionResponse.getResponse().getParams().getFraudResponse()).thenReturn(mock(PaymentProviderTransactionResponse.FraudResponse.class));
        when(paymentsSchema.getPayment()).thenReturn(mock(Payment.class));
        when(paymentsSchema.getPayment().getBilling()).thenReturn(mock(Billing.class));

        lenient().when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder().providerResponse(mock(ProviderResponse.class)).paymentId(UUID.randomUUID().toString()).build()));
        var paymentsResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertNotNull(paymentsResponse);
        assertNotNull(paymentsResponse.getPaymentId());
        assertNull(paymentsResponse.getProviderResponse());
    }

    @Test
    void testUpdateBookingReference() {
        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = new ProviderResponse();
        ThreeCResponse threeCResponse = new ThreeCResponse();

        threeCResponse.setProviderStatus("CS");
        providerResponse.setThreeCResponse(threeCResponse);
        lenient().when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(mock(PaymentsSchema.class))));
        lenient().when(paymentRepository.updatePaymentWithBookingReference(any(), any())).
                thenReturn(Mono.just(PaymentsSchema.builder().bookingReference(BOOKING_REFERENCE)
                        .providerResponse(providerResponse)
                        .build()));
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        var paymentResponse = defaultPaymentService.updateBookingReference(UpdateBookingReferenceRequest.builder().paymentId(paymentId).bookingReference(BOOKING_REFERENCE).build()).block();
        assertNotNull(paymentResponse);
        assertEquals(paymentId, paymentResponse.getPaymentId());
        assertEquals(BOOKING_REFERENCE, paymentResponse.getBookingReference());
    }

    @Test
    void testUpdateBookingReference_WhenPaymentIdNotFound() {
        var paymentId = UUID.randomUUID().toString();
        when(paymentRepository.getByPaymentId(any())).thenThrow(new PaymentServiceException(HttpStatus.NOT_FOUND, String.format(ERROR_MESSAGE, paymentId), ErrorCodes.PAYMENT_NOT_FOUND));
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> defaultPaymentService.updateBookingReference(UpdateBookingReferenceRequest.builder().paymentId(paymentId).bookingReference(BOOKING_REFERENCE).build()).block());
        assertEquals(ErrorCodes.PAYMENT_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testUpdateBookingReference_WhenPaymentIdIsNull() {
        when(paymentRepository.getByPaymentId(any())).thenThrow(new PaymentServiceException(HttpStatus.NOT_FOUND, ERROR_MESSAGE, ErrorCodes.PAYMENT_NOT_FOUND));
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> defaultPaymentService.updateBookingReference(UpdateBookingReferenceRequest.builder().bookingReference(BOOKING_REFERENCE).build()).block());
        assertEquals(ErrorCodes.PAYMENT_NOT_FOUND, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testRevisedSolutionDisabledForConfiguredHotelsInList(){

        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("CQ");
        when(providerResponse.getThreeCResponse().getProviderResult()).thenReturn("0000");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(false);

        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD","RE","AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");

        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(false, paymentResponse.isRevisedSolution());
    }

    @Test
    void testRevisedSolutionEnabledForConfiguredHotelsNotInList(){

        var paymentId = UUID.randomUUID().toString();
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);

        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("CQ");
        when(providerResponse.getThreeCResponse().getProviderResult()).thenReturn("0000");
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);

        when(revisedSolutionConfig.checkIfHotelConfiguredForRevisedSolution(paymentsSchema)).thenReturn(true);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));

        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD","RE","AR");
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");

        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(paymentsSchema.getProviderResponse()).thenReturn(providerResponse);
        var paymentResponse = defaultPaymentService.getPayment(paymentId,"").block();
        assertEquals(true, paymentResponse.isRevisedSolution());
    }

    @Test
    void testGetPaymentStatusForActionPoll_NoPaymentAttempt() {

        var paymentId = UUID.randomUUID().toString();
        var action = "poll";
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));

        var paymentResponse = defaultPaymentService.getPayment(paymentId,action).block();

        assertEquals(PaymentStatus.NO_PAYMENT_ATTEMPT.getStatus(), paymentResponse.getPaymentStatus());
        verify(threeCPaymentClient,times(0)).retrievePaymentProviderTransactionResponse(any());
    }

    @Test
    void testGetPaymentStatusForActionPoll_Success() {

        var paymentId = UUID.randomUUID().toString();
        var action = "poll";
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);

        when(paymentRepository.getByPaymentId(any())).thenReturn(Mono.just(Optional.of(paymentsSchema)));
        when(paymentsSchema.getProviderResponse()).thenReturn(mock(ProviderResponse.class));

        var paymentResponse = defaultPaymentService.getPayment(paymentId,action).block();

        assertNotNull(paymentResponse.getProviderResponse());
        assertEquals(PaymentStatus.SUCCESS.getStatus(), paymentResponse.getPaymentStatus());
        verify(threeCPaymentClient,times(0)).retrievePaymentProviderTransactionResponse(any());
    }

    @ParameterizedTest
    @MethodSource("createPaypalPaymentRequests")
    void testCreatePaypalPaymentWhenUnsuccessful(PaymentRequest paymentRequest) throws IOException {

        when(providerAccountFactory.getAccount(any())).thenReturn(mock(ProviderAccount.class));

        lenient().when(paymentRepository.createPaymentResource(any(), any()))
                .thenReturn(Mono.just(PaymentsSchema.builder()
                        .providerResponse(null)
                        .paymentId(ANY_PAYMENT_ID)
                        .booking(Booking.builder().businessSite(BusinessSite.builder().identifier("CARNOR").build()).build())
                        .template(TEMPLATE).build()));

        lenient().when(threeCPaymentClient.paypalRequest(any(PaymentRequest.class), any(), any()))
                .thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentResponse.json"), PaypalForwardAPITransactionResponse.class)));

        lenient().when(threeCPaymentClient.paypalMitRequest(any(), any(), any(), any()))
                .thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/paypal/responses/paypalPaymentResponse.json"), PaymentResponse.class))
                        .flatMap(mitResponse -> {
                            try {
                                return Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }));
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        lenient().when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder().providerResponse(mock(ProviderResponse.class))
                .paymentId(ANY_PAYMENT_ID)
                .providerResponse(providerResponse).build()
        ));
        defaultPaymentService.successResult = "101";
        defaultPaymentService.successTrxState = "AD";
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        when(paypalConfig.getTimeout()).thenReturn(30000L);
        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () ->
            defaultPaymentService.createPayment(paymentRequest).block()
        );
        String expectedMessage = "PayPal MIT Transaction Declined/Refused";
        String actualMessage = exception.getReason();
        assert actualMessage != null;
        assertEquals(ErrorCodes.PAYPAL_REFUSED, exception.getErrorCode());
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @ParameterizedTest
    @MethodSource("createPaypalPaymentRequests")
    void testCreatePaypalPaymentWhenSuccessful(PaymentRequest paymentRequest) throws IOException {

        when(providerAccountFactory.getAccount(any())).thenReturn(mock(ProviderAccount.class));

        lenient().when(paymentRepository.createPaymentResource(any(), any()))
                .thenReturn(Mono.just(PaymentsSchema.builder()
                        .providerResponse(null)
                        .paymentId(ANY_PAYMENT_ID)
                        .booking(Booking.builder().businessSite(BusinessSite.builder().identifier("CARNOR").build()).build())
                        .template(TEMPLATE).build()));

        lenient().when(threeCPaymentClient.paypalRequest(any(PaymentRequest.class), any(), any()))
                .thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentResponse.json"), PaypalForwardAPITransactionResponse.class)));

        lenient().when(threeCPaymentClient.paypalMitRequest(any(), any(), any(), any()))
                .thenReturn(Mono.just(objectMapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class)));

        ThreeCResponse threeCResponse = mock(ThreeCResponse.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        when(providerResponse.getThreeCResponse()).thenReturn(threeCResponse);
        when(threeCResponse.getProviderStatus()).thenReturn("APPROVED");
        when(threeCResponse.getProviderResult()).thenReturn("0");
        when(threeCResponse.getFraudCheckDecision()).thenReturn("ACCEPT");
        lenient().when(paymentRepository.updatePaymentResource(any(), any())).thenReturn(Mono.just(PaymentsSchema.builder().providerResponse(mock(ProviderResponse.class))
                .paymentId(ANY_PAYMENT_ID)
                .providerResponse(providerResponse).build()
        ));
        defaultPaymentService.fraudCheckAccept = List.of("ACCEPT", "");
        defaultPaymentService.noTransactionCode = List.of("711", "752");
        defaultPaymentService.successProviderStatus = List.of("CS", "CQ", "AA");
        defaultPaymentService.failureProviderStatus = List.of("AD", "RE", "AR");
        defaultPaymentService.successResult = "0";
        defaultPaymentService.successTrxState = "CQ";
        when(paypalConfig.getTimeout()).thenReturn(30000L);
        var paymentResponse = defaultPaymentService.createPayment(paymentRequest).block();

        assertNotNull (paymentResponse);
    }

    @Test
    void verify_MitResponseSuccess() {

        PaypalSuccessCodes successCodes = new PaypalSuccessCodes();
        successCodes.setResult("0");
        successCodes.setTrxState("AA");
        successCodes.setFraudInfoDecision("ACCEPT");
        when(paypalConfig.getSuccessCodes()).thenReturn(successCodes);

        PaypalForwardAPITransactionResponseBody responseBody = new PaypalForwardAPITransactionResponseBody();
        PaypalForwardAPITransactionResponseBody.Response response = new PaypalForwardAPITransactionResponseBody.Response();
        PaypalForwardAPITransactionResponseBody.Params params = new PaypalForwardAPITransactionResponseBody.Params();
        PaypalForwardAPITransactionResponseBody.CardFraudInfo cardFraudInfo = new PaypalForwardAPITransactionResponseBody.CardFraudInfo();
        cardFraudInfo.setDecision("ACCEPT");
        params.setResult("0");
        params.setTransactionState("AA");
        params.setCardFraudInfo(cardFraudInfo);
        response.setParams(params);
        responseBody.setResponse(response);

        var actual = defaultPaymentService.checkPaypalSuccessCodes(responseBody);

        assertThat(actual).isTrue();
    }

    @Test
    void verify_MitResponseFailure_WhenCardFraudInfoDecisionEmpty() {
        PaypalSuccessCodes successCodes = new PaypalSuccessCodes();
        successCodes.setResult("0");
        successCodes.setTrxState("AA");
        successCodes.setFraudInfoDecision("ACCEPT");
        when(paypalConfig.getSuccessCodes()).thenReturn(successCodes);

        PaypalForwardAPITransactionResponseBody responseBody = new PaypalForwardAPITransactionResponseBody();
        PaypalForwardAPITransactionResponseBody.Response response = new PaypalForwardAPITransactionResponseBody.Response();
        PaypalForwardAPITransactionResponseBody.Params params = new PaypalForwardAPITransactionResponseBody.Params();
        PaypalForwardAPITransactionResponseBody.CardFraudInfo cardFraudInfo = new PaypalForwardAPITransactionResponseBody.CardFraudInfo();
        cardFraudInfo.setDecision("");
        params.setResult("0");
        params.setTransactionState("AA");
        params.setCardFraudInfo(cardFraudInfo);
        response.setParams(params);
        responseBody.setResponse(response);

        var actual = defaultPaymentService.checkPaypalSuccessCodes(responseBody);

        assertThat(actual).isFalse();
    }

  @Test
  void testCreatePayment_MitCc_FullFlow() throws Exception {

    // Arrange
    PaymentRequest paymentRequest = objectMapper.readValue(
        new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMit_CC.json"),
        PaymentRequest.class
    );

    ProviderAccount account = mock(ProviderAccount.class);
    when(providerAccountFactory.getAccount(any())).thenReturn(account);

    when(paypalConfig.getTimeout()).thenReturn(5000L);

    // Initial createPaymentResource call (providerResponse = null triggers MIT_CC flow)
    when(paymentRepository.createPaymentResource(any(), any()))
        .thenReturn(Mono.just(
            PaymentsSchema.builder()
                .paymentId(ANY_PAYMENT_ID)
                .providerResponse(null)
                .template(TEMPLATE)
                .booking(Booking.builder()
                    .businessSite(BusinessSite.builder().identifier("CARNOR").build())
                    .build())
                .build()
        ));

    // --- Mock MIT_CC 3C Response (NoCardReadTransactionResponse) ---
    NoCardReadTransactionResponse.Params params = new NoCardReadTransactionResponse.Params();
    params.setProviderReference("MITCC-TXID-123");
    params.setResult("0");
    params.setTransactionState("CQ");


    NoCardReadTransactionResponse.Response wrapper =
        new NoCardReadTransactionResponse.Response();
    wrapper.setType("payrequestnocardread");
    wrapper.setVersion("W2MXG520");
    wrapper.setParams(params);

    NoCardReadTransactionResponse mitCcResponse =
        new NoCardReadTransactionResponse(wrapper);

    when(threeCPaymentClient.mitCcRequest(any(), any(), any()))
        .thenReturn(Mono.just(mitCcResponse));

    // --- Mock updated PaymentSchema after MIT_CC call ---
    ThreeCResponse updatedThreeCResponse = new ThreeCResponse();
    updatedThreeCResponse.setProviderStatus("CQ");   // MUST MATCH successTrxState
    updatedThreeCResponse.setProviderResult("0");    // MUST MATCH successResult
    updatedThreeCResponse.setStatus("SUCCESS");      // used only for logging

    ProviderResponse updatedProviderResponse = new ProviderResponse();
    updatedProviderResponse.setThreeCResponse(updatedThreeCResponse);

    PaymentsSchema updatedSchema = PaymentsSchema.builder()
        .paymentId(ANY_PAYMENT_ID)
        .providerResponse(updatedProviderResponse)
        .template(TEMPLATE)
        .booking(Booking.builder()
            .businessSite(BusinessSite.builder().identifier("CARNOR").build())
            .build())
        .build();

    lenient().when(paymentRepository.updatePaymentResource(any(), any()))
        .thenReturn(Mono.just(updatedSchema));

    // --- Required internal service fields ---
    defaultPaymentService.noTransactionCode = List.of("711", "752");
    defaultPaymentService.successProviderStatus = List.of("CQ", "RE");

    ReflectionTestUtils.setField(defaultPaymentService, "successResult", "0");
    ReflectionTestUtils.setField(defaultPaymentService, "successTrxState", "CQ");

    // Act
    PaymentResponse response = defaultPaymentService.createPayment(paymentRequest).block();

    // Assert
    assertNotNull(response);
    assertEquals(ANY_PAYMENT_ID, response.getPaymentId());
    assertEquals("0",
        response.getProviderResponse().getThreeCResponse().getProviderResult());

    // Verify MIT_CC flow was invoked
    verify(threeCPaymentClient).mitCcRequest(any(), any(), any());
  }


  @Test
  void testCreatePayment_MitCc_ErrorFrom3C() throws Exception {

    PaymentRequest paymentRequest = objectMapper.readValue(
        new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMit_CC.json"),
        PaymentRequest.class
    );

    ProviderAccount account = mock(ProviderAccount.class);
    when(providerAccountFactory.getAccount(any())).thenReturn(account);

    when(paypalConfig.getTimeout()).thenReturn(5000L);

    when(paymentRepository.createPaymentResource(any(), any()))
        .thenReturn(Mono.just(
            PaymentsSchema.builder()
                .paymentId(ANY_PAYMENT_ID)
                .providerResponse(null)
                .template(TEMPLATE)
                .booking(Booking.builder()
                    .businessSite(BusinessSite.builder().identifier("CARNOR").build())
                    .build())
                .build()
        ));

    when(threeCPaymentClient.mitCcRequest(any(), any(), any()))
        .thenReturn(Mono.error(
            new PaymentProcessingException(
                ErrorCode.DIGITAL_THREEC_MIT_CC_PAYMENT_EXCEPTION,
                "3C error"
            )
        ));

    StepVerifier.create(defaultPaymentService.createPayment(paymentRequest))
        .expectErrorMatches(ex ->
            ex instanceof PaymentProcessingException &&
                ((PaymentProcessingException) ex).getErrorCode() ==
                    ErrorCode.DIGITAL_THREEC_MIT_CC_PAYMENT_EXCEPTION.getCode()
        )
        .verify();
  }

  @Test
  void testCreatePayment_MitCc_Timeout() throws Exception {

    PaymentRequest paymentRequest = objectMapper.readValue(
        new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMit_CC.json"),
        PaymentRequest.class
    );

    ProviderAccount account = mock(ProviderAccount.class);
    when(providerAccountFactory.getAccount(any())).thenReturn(account);

    when(paypalConfig.getTimeout()).thenReturn(10L); // tiny timeout

    when(paymentRepository.createPaymentResource(any(), any()))
        .thenReturn(Mono.just(
            PaymentsSchema.builder()
                .paymentId(ANY_PAYMENT_ID)
                .providerResponse(null)
                .template(TEMPLATE)
                .booking(Booking.builder()
                    .businessSite(BusinessSite.builder().identifier("CARNOR").build())
                    .build())
                .build()
        ));

    // Simulate long-running MIT_CC call
    when(threeCPaymentClient.mitCcRequest(any(), any(), any()))
        .thenReturn(Mono.never());

    StepVerifier.create(defaultPaymentService.createPayment(paymentRequest))
        .expectErrorMatches(ex ->
            ex instanceof PaymentProcessingException &&
                ((PaymentProcessingException) ex).getErrorCode() ==
                    ErrorCode.DIGITAL_THREEC_TIMEOUT_EXCEPTION.getCode()
        )
        .verify();
  }

}