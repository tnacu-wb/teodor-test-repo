package uk.co.whitbread.payments.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.config.PaypalConfig;
import uk.co.whitbread.payments.config.PaypalSuccessCodes;
import uk.co.whitbread.payments.converters.*;
import uk.co.whitbread.payments.exception.ErrorCode;
import uk.co.whitbread.payments.exception.ErrorCodes;
import uk.co.whitbread.payments.exception.PaymentProcessingException;
import uk.co.whitbread.payments.exception.PaymentServiceException;
import uk.co.whitbread.payments.model.*;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.model.threec.CreateTokenResponse;
import uk.co.whitbread.payments.model.threec.InitialiseRequest;
import uk.co.whitbread.payments.model.threec.InitialiseResponse;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionRequest;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionRequest;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponse;
import uk.co.whitbread.payments.model.threec.PaypalForwardAPITransactionResponseBody;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdRequest;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdResponse;
import uk.co.whitbread.payments.model.threec.StartReconciliationResponse;
import uk.co.whitbread.payments.model.threec.UpdateTokenResponse;
import uk.co.whitbread.payments.properties.AccountConfigProperties;
import uk.co.whitbread.payments.properties.ProviderAccount;
import uk.co.whitbread.payments.properties.ThreeCProperties;
import uk.co.whitbread.payments.service.EMerchantService;
import uk.co.whitbread.payments.service.PaypalTokenService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreeCPaymentClientTest {

    private static final String PATH_TO_INITIALISE = "/path/to/initialise";
    private static final String PATH_TO_TRANSACTIONS = "/path/to/transactions";
    private static final String PATH_TO_TRANSACTIONAL_REFUND = "/path/to/refund";
    private static final String PATH_TO_RECONCILE = "/path/to/reconcile";
    private static final String IPG_SESSION = "6Y71NeaD1oEe0gP+Valdxfym1iyjao4xS645kSBp7FC88LSNZC2ukAET9RNoedJ0KKnBieck4gTH8CPQAFs5ayIsucisbToK6Ilu+i8MojS7jypUtPbT0/kFQ/G72GCILx+aqZJ/jsoyXFphXSml9ZXiQBJehMNOfGJpRvgqcAlBnG9qq/wucz8g3YgKQodXPzGFvzI3QbYWqmp4M6+Gv0R5X1O+uq2d/mWVjA8xzQ1beQ1TIfPKuIt+CEyLidjickJOUHkELC1UwcMMfFlkvBoZUlivkAWYiX3Heb4HHfk/hwLtcaRBlXO9CUt8h6kooP6isrZ875RRMAWEc8S5ES9F5/Cx7wPBje3sQAgyoTxTd2vGlBmMByy00WGT+HTSLWtT45mp7uEGyEnQHeq8aD/xrZ8j+t/PbVeakjXMRZZovWC5D5IZgSDCxcB9LUkPHq3Xu7qCExD+OCINkCZsc9nGEH5vmcbKhN+bPVKwa/LHdi2CTVSs806yrl3Q8QuFUjUH4Gt57WaCECkZF5dV9pJst4bqGWv6fgxcLyR/mBqG9Qgdu85mO5A4t8cHbPIgiJ3jGICxWg1JFmf3HvtvykdP6mddz2j6RY8UomY/8RshA41sWVPbKtQh9Thr+jC3l36OGpcgsWcY1M6kTp3+KT0/v5PexyZ15PTQf8glma/vhPhKVW35bgrlJ5tEJOyIqvkTd9ZZ+IK6Cx1hxR9k5QkipvqldNN5AAbjydJJ/fo6CvOM81L0jufQhYeeVm9vBYk+tLigExzbipqAuE6XpQ8TN0uuYKMy4QPymgr32WUSbWqB7JZj73bE7qI+uT+hZ739+dFk5wGErPyTSNE5lwv1FwHeiyWZIuju5Drw03YjGPYol3n+joVNGVnA0K+kHzJme945mjVXDSfgaBAzlNnY6qBWgCf4XpOTi5Sf5yuYZn3WY76I/Y99V6VdbsG/UU9oKP59i03DPv0rj5qLPgKLTLSEg2rym6lu/ptDK45HNGPmjUBJUsEjDWvhKzGekqQ4HT8NwH7/634zw7uguLV1qEGscB+1ujYXR8Xnfl80trXam/G/vxwVERcDwGd+2vSETD8hog0ERJBp76T5lBP375w3ZTL9mbVO1w8MGKfl25GWYr4m+bh+kLqyGnrBt5+e57+VbSrh2/vM6Nbq+b/oHoUmzo1ZF9MnxjhQhWOqCCn0mDuGOahK4IbrZ+lx";
    private static final String ANY_PAYMENT_ID = "123456789D";
    private static final String MOCK_CARD_TEMPLATE = "mockCardTemplate";

    @Mock
    private ThreeCProperties threeCProperties;


    @Mock
    private ThreeCProperties.Endpoints endpoints;

    @Mock
    private ThreeCTransformer threeCTransformer;

    @Mock
    private EMerchantService eMerchantService;

    @Mock
    private PaypalTokenService paypalTokenService;

    @Mock
    private TokenRequestMapper tokenRequestMapper;

    private ThreeCPaymentClient threeCPaymentClient;

    private ObjectMapper mapper;
    private MockWebServer mockWebServer;
    @Mock
    private PaypalConfig paypalConfig;


    @BeforeEach
    void setUp() throws IOException {
        mapper = new ObjectMapper().findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient threeCWebClient = WebClient.create(String.format("http://localhost:%s", mockWebServer.getPort()));
        threeCPaymentClient = new ThreeCPaymentClient(threeCWebClient, mapper, threeCProperties, threeCTransformer, eMerchantService, paypalConfig, paypalTokenService);
    }

    @AfterEach
    void tearDown() {
        mockWebServer.close();
    }

    @Test
    void verifyMappingErrorOnInitialise() {
        when(threeCTransformer.populateInitialiseRequest(any(PaymentRequest.class), any(), any(), any())).thenReturn(new InitialiseRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getInitialise()).thenReturn(PATH_TO_INITIALISE);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("Response missing key fields.")
                        .build()
        );

        try {
            threeCPaymentClient.initialiseIpage(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class), ANY_PAYMENT_ID, MOCK_CARD_TEMPLATE, getProviderAccount()).block();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof PaymentServiceException);
            var exception = (PaymentServiceException) e;
            assertEquals(ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE, exception.getErrorCode());
        }
    }

    @Test
    void verifySuccessfulInitialise() throws IOException {
        when(threeCTransformer.populateInitialiseRequest(any(PaymentRequest.class), any(), any(), any())).thenReturn(new InitialiseRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getInitialise()).thenReturn(PATH_TO_INITIALISE);
        var initialiseSuccessResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/initialiseIpageResponse.json"), String.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );

        InitialiseResponse actualResponse = threeCPaymentClient.initialiseIpage(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class), ANY_PAYMENT_ID, MOCK_CARD_TEMPLATE, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals(IPG_SESSION, actualResponse.getIpgSession());
    }

    @Test
    void verifySuccessfulSaveCardInitialise() throws IOException {
        when(threeCTransformer.populateInitialiseRequest(any(SaveCardRequest.class), any(), any(), any())).thenReturn(new InitialiseRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getInitialise()).thenReturn(PATH_TO_INITIALISE);
        var initialiseSuccessResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/initialiseIpageResponse.json"), String.class);

        mockWebServer.enqueue(
            new MockResponse.Builder()
                .code(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(initialiseSuccessResponse)
                .build()
        );

        InitialiseResponse actualResponse = threeCPaymentClient.initialiseIpage(mapper.readValue(new File("src/test/resources/stubs/3c/requests/saveCardRequest.json"), SaveCardRequest.class), ANY_PAYMENT_ID, MOCK_CARD_TEMPLATE, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals(IPG_SESSION, actualResponse.getIpgSession());
    }

    @Test
    void verifySuccessfulPayRequestNoCardRead() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populateNoCardReadRequest(any(PaymentRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        NoCardReadTransactionResponse actualResponse = threeCPaymentClient.noCardReadRequest(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class), ANY_PAYMENT_ID, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals("payrequestnocardread", actualResponse.getResponse().getType());
        assertEquals("17193188-1645-4B28-A1B6-ACC61BC04E8E", actualResponse.getResponse().getParams().getProviderReference());
    }

    @Test
    void shouldThrowPaymentExceptionWhenUnsuccessfulPayRequestNoCardRead() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populateNoCardReadRequest(any(PaymentRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("Some error")
                        .build()
        );

        try {
            threeCPaymentClient.noCardReadRequest(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class), ANY_PAYMENT_ID, getProviderAccount()).block();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof PaymentServiceException);
            var paymentServiceException = (PaymentServiceException) e;
            assertEquals(ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE, paymentServiceException.getErrorCode());
        }
    }

    @Test
    void verifySuccessfulAuthoriseRequestNoCardRead() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populateNoCardReadRequest(any(PaymentRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        var authoriseRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/authoriseRequestNoCardReadResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(authoriseRequestNoCardReadResponse))
                        .build()
        );

        NoCardReadTransactionResponse actualResponse = threeCPaymentClient.noCardReadRequest(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class), ANY_PAYMENT_ID, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals("authoriserequestnocardread", actualResponse.getResponse().getType());
        assertEquals("17193188-1645-4B28-A1B6-ACC61BC04E8E", actualResponse.getResponse().getParams().getProviderReference());
    }

    @Test
    void verifySuccessfulReverseByTransactionId() throws IOException {
        when(threeCTransformer.populateReverseByTransactionIdRequest(any())).thenReturn(new ReverseByTransactionIdRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getRefund()).thenReturn(PATH_TO_TRANSACTIONAL_REFUND);
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentsSchema.getProviderResponse()).thenReturn(mock(ProviderResponse.class));
        var reverseByTransactionIdResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/reverseByTransactionIdResponse.json"), ReverseByTransactionIdResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(reverseByTransactionIdResponse))
                        .build()
        );

        ReverseByTransactionIdResponse actualResponse = threeCPaymentClient.refund(paymentsSchema).block();

        assertNotNull(actualResponse);
        assertEquals("payreversebytxid", actualResponse.getResponse().getType());
        assertEquals("E92C0DCD-D845-448B-AD79-E3345A663C96", actualResponse.getResponse().getParams().getProviderReference());
    }

    @Test
    void verifySuccessfulSiteReconcile() throws IOException {
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getReconciliation()).thenReturn(PATH_TO_RECONCILE);

        EMerchantDetails eMerchantDetails = new EMerchantDetails("WhitbreadTestLondonHotel", "WhitbreadTestLondonHotel1");
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);

        var startReconciliationResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/reconciliationResponse.json"), StartReconciliationResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(startReconciliationResponse))
                        .build()
        );

        StartReconciliationResponse actualResponse = threeCPaymentClient.startReconciliation("LONHOL", PaymentSubType.ECOMM.name()).block();

        assertNotNull(actualResponse);
        assertEquals(0, actualResponse.getReturnCode());
        assertEquals("Success", actualResponse.getReturnText());
    }

    @Test
    void verifyPaymentExceptionWhenReconciliationError() {
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getReconciliation()).thenReturn(PATH_TO_RECONCILE);
        EMerchantDetails eMerchantDetails = new EMerchantDetails("username", "password");
        when(eMerchantService.getEMerchantDetails(any(), any())).thenReturn(eMerchantDetails);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
        );

        try {
            threeCPaymentClient.startReconciliation("LONHOL", PaymentSubType.MOTO.name()).block();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof PaymentServiceException);
            var exception = (PaymentServiceException) e;
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        }
    }

    @Test
    void verifySuccessfulAuthorizeScaInitialise() throws IOException {
        when(threeCTransformer.populateInitialiseAuthorizeScaRequest(anyString(), any(), anyString(), anyString())).thenReturn(new InitialiseRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getInitialise()).thenReturn(PATH_TO_INITIALISE);
        var initialiseSuccessResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/initialiseIpageResponse.json"), String.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var authorizeScaRequest = AuthorizeScaRequest.builder()
                .requestId(UUID.randomUUID().toString()).environment("LOCAL").language("en")
            .country("de").bookingReference("GAA8350663").build();
        InitialiseResponse actualResponse = threeCPaymentClient.initialiseAuthorizeScaIpage(authorizeScaRequest, ANY_PAYMENT_ID, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals(IPG_SESSION, actualResponse.getIpgSession());
    }

    @Test
    void verifyAuthorizeScaInitialise_MalformedResponse() {
        when(
            threeCTransformer.populateInitialiseAuthorizeScaRequest(anyString(), any(), anyString(),
                anyString()))
            .thenReturn(new InitialiseRequest());

        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getInitialise()).thenReturn(PATH_TO_INITIALISE);

        mockWebServer.enqueue(
            new MockResponse.Builder()
                .code(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("INVALID_JSON")
                .build()
        );

        var authorizeScaRequest = AuthorizeScaRequest.builder()
            .requestId(UUID.randomUUID().toString()).environment("LOCAL").language("en")
            .country("de").bookingReference("GAA8350663").build();

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> {
            threeCPaymentClient.initialiseAuthorizeScaIpage(authorizeScaRequest, ANY_PAYMENT_ID,
                getProviderAccount()).block();
        });

        assertEquals(ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Could not map Initialise response"));
    }

    @Test
    void verifyAuthorizeScaInitialise_InternalServerError() {
        when(
            threeCTransformer.populateInitialiseAuthorizeScaRequest(anyString(), any(), anyString(),
                anyString()))
            .thenReturn(new InitialiseRequest());

        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getInitialise()).thenReturn(PATH_TO_INITIALISE);

        mockWebServer.enqueue(
            new MockResponse.Builder()
                .code(500)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body("{\"error\": \"Internal Server Error\"}")
                .build()
        );

        var authorizeScaRequest = AuthorizeScaRequest.builder()
            .requestId(UUID.randomUUID().toString()).environment("LOCAL").language("en")
            .country("de").bookingReference("GAA8350663").build();

        assertThrows(Exception.class, () -> {
            threeCPaymentClient.initialiseAuthorizeScaIpage(authorizeScaRequest, ANY_PAYMENT_ID,
                getProviderAccount()).block();
        });
    }

    private ProviderAccount getProviderAccount() {
        var providerAccount = new ProviderAccount();
        var accountConfig = new AccountConfigProperties();
        providerAccount.setCurrency(Currency.GBP);
        providerAccount.setPaymentType(PaymentType.CARD);
        providerAccount.setBookingType(BookingType.PAY_NOW);
        providerAccount.setChannelTypes(List.of(ChannelType.PI));
        providerAccount.setPaymentSubTypes(List.of(PaymentSubType.ECOMM));
        accountConfig.setServiceAction("testServiceAction");
        accountConfig.setNewCardTemplate("iPageTemplate");
        providerAccount.setConfiguration(accountConfig);
        return providerAccount;
    }

    @Test
    void verifySuccessfulRefundRequestNoCardRead() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        var params = new NoCardReadTransactionRequest.Params();
        params.setAmount("-1000");
        request.setParams(params);
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populateNoCardReadRequest(any(RefundRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        NoCardReadTransactionResponse actualResponse = threeCPaymentClient.noCardReadRequest(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class), ANY_PAYMENT_ID, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals("payrequestnocardread", actualResponse.getResponse().getType());
        assertEquals("CDB0C14C-580F-4CA2-8CBA-122A6A824857", actualResponse.getResponse().getParams().getProviderReference());
        assertEquals("-1000", actualResponse.getResponse().getParams().getAmount());
    }

    @Test
    void shouldThrowPaymentExceptionWhenUnsuccessfulRefundRequestNoCardRead() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populateNoCardReadRequest(any(RefundRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("Some error")
                        .build()
        );

        try {
            threeCPaymentClient.noCardReadRequest(mapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class), ANY_PAYMENT_ID, getProviderAccount()).block();
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof PaymentServiceException);
            var paymentServiceException = (PaymentServiceException) e;
            assertEquals(ErrorCodes.UNABLE_TO_PARSE_PROVIDER_RESPONSE, paymentServiceException.getErrorCode());
        }
    }

    @Test
    void verifyRetrievalOfPaymentProviderTransactionResponse() throws IOException {
        when(threeCTransformer.populatePaymentProviderTransactionRequest(any())).thenReturn(new PaymentProviderTransactionRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentsSchema.getPaymentId()).thenReturn("paymentId");
        var paymentProviderTransactionResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/3c/responses/paymentProviderTransactionResponse.json"), PaymentProviderTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(paymentProviderTransactionResponse))
                        .build()
        );

        PaymentProviderTransactionResponse actualResponse = threeCPaymentClient.retrievePaymentProviderTransactionResponse(paymentsSchema).block();
        assertNotNull(actualResponse);
        assertEquals("getstatusbymerchantref", actualResponse.getResponse().getType());
        assertEquals("F56AAA00-310C-4B1B-A00E-9383E62609D2", actualResponse.getResponse().getParams().getProviderReference());
    }

    @Test
    void shouldReturnEmptyResponseWhileRetrievingPaymentProviderTransactionResponse() {
        when(threeCTransformer.populatePaymentProviderTransactionRequest(any())).thenReturn(new PaymentProviderTransactionRequest());
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        PaymentsSchema paymentsSchema = mock(PaymentsSchema.class);
        when(paymentsSchema.getPaymentId()).thenReturn("paymentId");
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("")
                        .build()
        );
        var response = threeCPaymentClient.retrievePaymentProviderTransactionResponse(paymentsSchema).block();
        assertNull(response);
    }

    @Test
    void verifySuccessfulPaypalMitRequest() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populatePaypalMITRequest(any(), any(PaymentRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setResult("0");
        paypalSuccessCodes.setTrxState("AA");
        paypalSuccessCodes.setFraudInfoDecision("ACCEPT");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);
        NoCardReadTransactionResponse actualResponse = threeCPaymentClient.paypalMitRequest(
                mapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentResponse.json"), PaypalForwardAPITransactionResponse.class),
                mapper.readValue(new File("src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"), PaymentRequest.class),
                ANY_PAYMENT_ID, getProviderAccount()).block();
        assertNotNull(actualResponse);
        assertEquals("payrequestnocardread", actualResponse.getResponse().getType());
        assertEquals("W2MXG520", actualResponse.getResponse().getVersion());
        assertEquals("63546334525D", actualResponse.getResponse().getParams().getTransactionReference());
        assertEquals("APPROVED", actualResponse.getResponse().getParams().getReason());
        assertEquals("ACCEPT", actualResponse.getResponse().getParams().getFraudResponse().getDecision());
    }

    @Test
    void shouldThrowPaymentExceptionWhenUnsuccessfulPaypalMitRequest() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        when(threeCTransformer.populatePaypalMITRequest(any(), any(PaymentRequest.class), any(), any())).thenReturn(noCardReadTransactionRequest);
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setResult("0");
        paypalSuccessCodes.setTrxState("AA");
        paypalSuccessCodes.setFraudInfoDecision("ACCEPT");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> threeCPaymentClient.paypalMitRequest(mapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentResponse.json"), PaypalForwardAPITransactionResponse.class),
                mapper.readValue(new File("src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"), PaymentRequest.class),
                ANY_PAYMENT_ID, getProviderAccount()).block());
        String expectedMessage = "Error encountered during paypal mit transaction for payment with id 123456789D.";
        String actualMessage = exception.getReason();
        assert actualMessage != null;
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowPaymentExceptionWhenNot200PaypalMitRequest() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        PaymentServiceException exception = assertThrows(PaymentServiceException.class,
                () -> threeCPaymentClient.paypalMitRequest(
                        mapper.readValue(new File(
                                "src/test/resources/stubs/paypal/responses/createPaypalPaymentResponse500.json"), PaypalForwardAPITransactionResponse.class),
                        mapper.readValue(new File(
                                "src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"), PaymentRequest.class),
                        ANY_PAYMENT_ID, getProviderAccount()).block());
        String expectedMessage = "Unable to proceed paypal MIT transaction [500]";
        String actualMessage = exception.getReason();
        assert actualMessage != null;
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowPaymentExceptionWhenProviderResponseProblemPaypalMitRequest() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        PaymentServiceException exception = assertThrows(PaymentServiceException.class,
                () -> threeCPaymentClient.paypalMitRequest(
                        mapper.readValue(new File(
                                "src/test/resources/stubs/paypal/responses/createPaypalPaymentResponseErrorBody.json"), PaypalForwardAPITransactionResponse.class),
                        mapper.readValue(new File(
                                "src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"), PaymentRequest.class),
                        ANY_PAYMENT_ID, getProviderAccount()).block());
        String expectedMessage = "Unable to proceed paypal MIT transaction [200]";
        String actualMessage = exception.getReason();
        assert actualMessage != null;
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shouldThrowPaymentExceptionWhenRefusedPaypalForwardingResponse() throws IOException {
        var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
        var request = new NoCardReadTransactionRequest.Request();
        request.setParams(new NoCardReadTransactionRequest.Params());
        noCardReadTransactionRequest.setRequest(request);
        var payRequestNoCardReadResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentMitResponse.json"), NoCardReadTransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(payRequestNoCardReadResponse))
                        .build()
        );

        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setResult("0");
        paypalSuccessCodes.setTrxState("AA");
        paypalSuccessCodes.setFraudInfoDecision("REFUSED");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> threeCPaymentClient.paypalMitRequest(mapper.readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentResponseRefused.json"), PaypalForwardAPITransactionResponse.class),
                mapper.readValue(new File("src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"), PaymentRequest.class),
                ANY_PAYMENT_ID, getProviderAccount()).block());
        String expectedMessage = "PayPal Forwarding API Transaction Declined/Refused [200].";
        String actualMessage = exception.getReason();
        assert actualMessage != null;
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void verifyUnSuccessfulPaypalRequest() throws IOException {

        var paypalForwardAPITransactionResponse = mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(new File("src/test/resources/stubs/paypal/responses/createPaypalPaymentResponse.json"), PaypalForwardAPITransactionResponse.class);

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(mapper.writeValueAsString(paypalForwardAPITransactionResponse))
                        .build()
        );

        when(paypalConfig.getEnvironment()).thenReturn("sandbox");
        when(paypalConfig.getMerchantID()).thenReturn("by3nnczb5byhtstn");
        when(paypalConfig.getPublicKey()).thenReturn("74cb2zxcmn5jbhjf");
        when(paypalConfig.getPrivateKey()).thenReturn("19a732cdc587d23cc502f8988b17a85f");

        PaymentServiceException exception = assertThrows(PaymentServiceException.class, () -> threeCPaymentClient.paypalRequest(mapper.readValue(new File("src/test/resources/stubs/paypal/requests/createPaypalPaymentRequest.json"), PaymentRequest.class),
                ANY_PAYMENT_ID, getProviderAccount()).block());
        String expectedMessage = "Customer ID has already been taken.: 123456789D";
        String actualMessage = exception.getReason();
        assert actualMessage != null;
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void checkPaypalSuccessCodeTestEmptyFraudDecision() {
        PaypalForwardAPITransactionResponseBody input = new PaypalForwardAPITransactionResponseBody();
        input.setResponse(new PaypalForwardAPITransactionResponseBody.Response());
        input.getResponse().setParams(new PaypalForwardAPITransactionResponseBody.Params());
        PaypalForwardAPITransactionResponseBody.CardFraudInfo cardFraudInfo = new PaypalForwardAPITransactionResponseBody.CardFraudInfo();
        cardFraudInfo.setDecision("");
        input.getResponse().getParams().setResult("0");
        input.getResponse().getParams().setTransactionState("CQ");
        input.getResponse().getParams().setCardFraudInfo(cardFraudInfo);
        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setFraudInfoDecision("ACCEPT");
        paypalSuccessCodes.setTrxState("CQ");
        paypalSuccessCodes.setResult("0");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);
        assertTrue(threeCPaymentClient.checkPaypalSuccessCodes(input));
    }

    @Test
    void checkPaypalSuccessCodeTestHappyPath() {
        PaypalForwardAPITransactionResponseBody input = new PaypalForwardAPITransactionResponseBody();
        input.setResponse(new PaypalForwardAPITransactionResponseBody.Response());
        input.getResponse().setParams(new PaypalForwardAPITransactionResponseBody.Params());
        PaypalForwardAPITransactionResponseBody.CardFraudInfo cardFraudInfo = new PaypalForwardAPITransactionResponseBody.CardFraudInfo();
        cardFraudInfo.setDecision("ACCEPT");
        input.getResponse().getParams().setResult("0");
        input.getResponse().getParams().setTransactionState("CQ");
        input.getResponse().getParams().setCardFraudInfo(cardFraudInfo);
        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setFraudInfoDecision("ACCEPT");
        paypalSuccessCodes.setTrxState("CQ");
        paypalSuccessCodes.setResult("0");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);
        assertTrue(threeCPaymentClient.checkPaypalSuccessCodes(input));
    }

    @Test
    void checkPaypalSuccessCodeNegativeFraudDecision() {
        PaypalForwardAPITransactionResponseBody input = new PaypalForwardAPITransactionResponseBody();
        input.setResponse(new PaypalForwardAPITransactionResponseBody.Response());
        input.getResponse().setParams(new PaypalForwardAPITransactionResponseBody.Params());
        PaypalForwardAPITransactionResponseBody.CardFraudInfo cardFraudInfo = new PaypalForwardAPITransactionResponseBody.CardFraudInfo();
        cardFraudInfo.setDecision("REFUSED");
        input.getResponse().getParams().setResult("0");
        input.getResponse().getParams().setTransactionState("CQ");
        input.getResponse().getParams().setCardFraudInfo(cardFraudInfo);
        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setFraudInfoDecision("ACCEPT");
        paypalSuccessCodes.setTrxState("CQ");
        paypalSuccessCodes.setResult("0");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);
        assertFalse(threeCPaymentClient.checkPaypalSuccessCodes(input));
    }

    @Test
    void checkPaypalSuccessCodeInvalidResultAndTransactionStatus() {
        PaypalForwardAPITransactionResponseBody input = new PaypalForwardAPITransactionResponseBody();
        input.setResponse(new PaypalForwardAPITransactionResponseBody.Response());
        input.getResponse().setParams(new PaypalForwardAPITransactionResponseBody.Params());
        PaypalForwardAPITransactionResponseBody.CardFraudInfo cardFraudInfo = new PaypalForwardAPITransactionResponseBody.CardFraudInfo();
        cardFraudInfo.setDecision("ACCEPT");
        input.getResponse().getParams().setResult("1");
        input.getResponse().getParams().setTransactionState("RJ");
        input.getResponse().getParams().setCardFraudInfo(cardFraudInfo);
        PaypalSuccessCodes paypalSuccessCodes = new PaypalSuccessCodes();
        paypalSuccessCodes.setFraudInfoDecision("ACCEPT");
        paypalSuccessCodes.setTrxState("CQ");
        paypalSuccessCodes.setResult("0");
        when(paypalConfig.getSuccessCodes()).thenReturn(paypalSuccessCodes);
        assertFalse(threeCPaymentClient.checkPaypalSuccessCodes(input));
    }

    @Test
    void verifySuccessfulCreateToken() {
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTokenCreate()).thenReturn(PATH_TO_TRANSACTIONS);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("eMerchantID", "eMerchantID");
        formData.add("ValidationCode", "ValidationCode");
        formData.add("TokenSchemeID", "");
        formData.add("TokenExpiryYYMM", "");

        when(threeCTransformer.populateCreateTokenFormData(any())).thenReturn(formData);

        Mono<CreateTokenResponse> actualResponse = threeCPaymentClient.createToken(new CreateTokenRequest());
        assertNotNull(actualResponse);
    }

    @Test
    void verifySuccessfulUpdateToken() {
        when(threeCProperties.getEndpoints()).thenReturn(endpoints);
        when(endpoints.getTokenUpdate()).thenReturn(PATH_TO_TRANSACTIONS);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("eMerchantID", "eMerchantID");
        formData.add("ValidationCode", "ValidationCode");
        formData.add("TokenSchemeID", "");
        formData.add("TokenExpiryYYMM", "");

        when(threeCTransformer.populateUpdateTokenFormData(any())).thenReturn(formData);

        Mono<UpdateTokenResponse> actualResponse = threeCPaymentClient.updateToken(new UpdateTokenRequest());
        assertNotNull(actualResponse);
    }


    @Test
    void verifySuccessfulMitCcRequest() throws IOException {
      // Arrange
      var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
      var request = new NoCardReadTransactionRequest.Request();
      request.setParams(new NoCardReadTransactionRequest.Params());
      noCardReadTransactionRequest.setRequest(request);

      when(threeCTransformer.populateMitCcRequest(any(PaymentRequest.class), any(), any()))
          .thenReturn(noCardReadTransactionRequest);

      when(threeCProperties.getEndpoints()).thenReturn(endpoints);
      when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);

      // Load stubbed MIT‑CC response JSON (same pattern as existing test)
      var mitCcResponse = mapper
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
          .readValue(
              new File("src/test/resources/stubs/3c/responses/payRequestMitCCNoCardReadResponse.json"),
              NoCardReadTransactionResponse.class
          );

      mockWebServer.enqueue(
          new MockResponse.Builder()
              .code(200)
              .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .body(mapper.writeValueAsString(mitCcResponse))
              .build()
      );

      // Act
      NoCardReadTransactionResponse actualResponse =
          threeCPaymentClient.mitCcRequest(
              mapper.readValue(
                  new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMit_CC.json"),
                  PaymentRequest.class
              ),
              ANY_PAYMENT_ID,
              getProviderAccount()
          ).block();

      // Assert
      assertNotNull(actualResponse);
      assertEquals("payrequestnocardread", actualResponse.getResponse().getType());
      assertEquals(
          "4ECB144A-8E56-4DA8-AD14-69BA09E08881",
          actualResponse.getResponse().getParams().getProviderReference()
      );
    }


  @Test
  void verifyErrorOnMitCcRequest() throws IOException {
    // Arrange
    var noCardReadTransactionRequest = new NoCardReadTransactionRequest();
    var request = new NoCardReadTransactionRequest.Request();
    request.setParams(new NoCardReadTransactionRequest.Params());
    noCardReadTransactionRequest.setRequest(request);

    when(threeCTransformer.populateMitCcRequest(any(PaymentRequest.class), any(), any()))
        .thenReturn(noCardReadTransactionRequest);

    when(threeCProperties.getEndpoints()).thenReturn(endpoints);
    when(endpoints.getTransactions()).thenReturn(PATH_TO_TRANSACTIONS);

    // Mock 3C returning HTTP 500
    mockWebServer.enqueue(
        new MockResponse.Builder()
            .code(500)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body("Internal Server Error")
            .build()
    );

    try {
      threeCPaymentClient.mitCcRequest(
          mapper.readValue(
              new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMit_CC.json"),
              PaymentRequest.class
          ),
          ANY_PAYMENT_ID,
          getProviderAccount()
      ).block();

      fail("Expected exception");
    } catch (Exception e) {

      // unwrap WebClientResponseException wrapper
      Throwable root = unwrap(e);

      // ⭐ EXPECT PaymentProcessingException (your new exception)
      assertTrue(root instanceof PaymentProcessingException);

      PaymentProcessingException ppe = (PaymentProcessingException) root;

      // ⭐ assert correct error code
      assertEquals(
          ErrorCode.DIGITAL_THREEC_MIT_CC_PAYMENT_EXCEPTION.getCode(),
          ppe.getErrorCode()
      );

      // ⭐ assert message contains your production message
      assertTrue(
          ppe.getDebugMessage().contains("Error encountered during MIT_CC transaction")
      );
    }
  }


  private Throwable unwrap(Throwable e) {
      Throwable t = e;
      while (t.getCause() != null && t != t.getCause()) {
        t = t.getCause();
      }
      return t;
    }
}

