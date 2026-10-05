package uk.co.whitbread.payments.routes;

import mockwebserver3.MockResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.xml.sax.SAXException;
import uk.co.whitbread.payments.integration.IntegrationBaseIT;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.CreateTokenResponse;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.ReconciliationRequest;
import uk.co.whitbread.payments.model.RefundRequest;
import uk.co.whitbread.payments.model.RefundResponse;
import uk.co.whitbread.payments.model.UpdateBookingReferenceRequest;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenResponse;
import uk.co.whitbread.payments.model.WebHookEckohFormData;
import uk.co.whitbread.payments.model.threec.NoCardReadTransactionResponse;
import uk.co.whitbread.payments.model.threec.PaymentProviderTransactionResponse;
import uk.co.whitbread.payments.model.threec.ReverseByTransactionIdResponse;
import uk.co.whitbread.payments.model.threec.StartReconciliationResponse;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext
class RoutesTestIT extends IntegrationBaseIT {

    private static final String BOOKING_REFERENCE = "ANY_BOOKING_REFERENCE";
    
    @Value("${whitbread.api.key}")
    private String apiKeyName;
    @Value("${whitbread.api.value}")
    private String apiKeyValue;

    @Test
    void testActuatorHealthCheck() throws IOException {
        var successResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/actuatorHealthCheckResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(successResponse)
                        .build()
        );
        webTestClient
                .get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("status")
                .isEqualTo("UP");
    }

    @Test
    void testCreateEcommercePayment() throws IOException {
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("paymentId")
                .isNotEmpty()
                .jsonPath("providerResponse")
                .isNotEmpty()
                .jsonPath("providerResponse.threecResponse.providerUrl")
                .doesNotExist()
                .jsonPath("providerResponse.threecResponse.sessionId")
                .isNotEmpty();
    }

    @Test
    void testCreateEcommercePaymentWithDefaultEmailAndCountry() throws IOException {
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest_no_email.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("paymentId")
                .isNotEmpty()
                .jsonPath("providerResponse")
                .isNotEmpty()
                .jsonPath("providerResponse.threecResponse.providerUrl")
                .doesNotExist()
                .jsonPath("providerResponse.threecResponse.sessionId")
                .isNotEmpty();
    }

    @Test
    void testCreateEcommercePibaPayment() throws IOException {
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestPIBA.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("paymentId")
                .isNotEmpty()
                .jsonPath("providerResponse")
                .isNotEmpty()
                .jsonPath("providerResponse.threecResponse.providerUrl")
                .doesNotExist()
                .jsonPath("providerResponse.threecResponse.sessionId")
                .isNotEmpty();
    }

    @Test
    void testCreateMotoPayment() throws IOException {
        var initialiseSuccessResponseMoto = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadResponse.json"), NoCardReadTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(initialiseSuccessResponseMoto))
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("Response")
                .isNotEmpty()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isNotEmpty()
                .jsonPath("Response.Params.SCATransRef")
                .isEqualTo("BR260692A");
    }

    @Test
    void testCreateMotoPaymentIdempotency() throws IOException {
        //Given Request 1
        var noCardReadTransactionResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/createMotoPaymentIdempotencyResponse.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(noCardReadTransactionResponse))
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        var paymentId = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10))
                .getPaymentId();

        // When Request 2 is sent with the same transaction reference
        var noCardReadTransactionResponseTwo = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/createMotoPaymentIdempotencyResponse.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(noCardReadTransactionResponseTwo))
                        .build()
        );
        var paymentRequest2 = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class);
        paymentRequest2.setRequestId(requestId);
        var result = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest2))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        //Then the payment id should be the same
        assertNotNull(result);
        assertEquals(paymentId, result.getPaymentId());
    }

    @Test
    void testCreatePaymentEnvironmentMandatoryForEcomm() throws IOException {
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/createPaymentEnvironmentMandatoryForEcomm.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestMissingEnvironment.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty();
    }

    @Test
    void testCreateEckohPayment() throws IOException {
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/createEckohPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);

        var noCardReadTransactionResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadResponse.json"), NoCardReadTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(noCardReadTransactionResponse))
                        .build()
        );

        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("Response")
                .isNotEmpty()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isNotEmpty()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isEqualTo("test11118");
    }

    @Test
    void testSuccessfulPaymentResourceEckohWebhook() throws IOException {
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/createEckohPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);

        var eckohWebhookResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/successfulPaymentResourceEckohWebhook.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(eckohWebhookResponse))
                        .build()
        );

        var paymentResponse = webTestClient
            .post()
            .uri("/payments")
            .body(BodyInserters.fromValue(paymentRequest)).exchange()
            .returnResult(PaymentResponse.class)
            .getResponseBody()
            .blockFirst(Duration.ofSeconds(10));

        var paymentId = paymentResponse.getPaymentId();

        var eckohWebhookRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/eckohSuccessfulWebhookRequest.json"), WebHookEckohFormData.class);
        eckohWebhookRequest.setReference(paymentId);

        var eckohWebhookSuccessfulResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/eckohSuccessfulWebhookResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(204)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
        );

        var response = webTestClient
            .post()
            .uri(String.format("/payments/eckoh/webhook"))
            .body(BodyInserters.fromValue(eckohWebhookRequest))
            .exchange();

        response.expectStatus().isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testFailurePaymentResourceEckohWebhook() throws IOException {
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/createEckohPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);

        var eckohWebhookResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/failurePaymentResourceEckohWebhook.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(eckohWebhookResponse))
                        .build()
        );

        var paymentResponse = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        var paymentId = paymentResponse.getPaymentId();

        var eckohWebhookRequest = objectMapper.readValue(new File("src/test/resources/stubs/eckoh/requests/eckohFailureWebhookRequest.json"), WebHookEckohFormData.class);
        eckohWebhookRequest.setReference(paymentId);

        var eckohWebhookFailureResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/eckohFailureWebhookResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(404)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(eckohWebhookFailureResponse)
                        .build()
        );

        webTestClient
                .post()
                .uri(String.format("/payments/eckoh/webhook"))
                .body(BodyInserters.fromValue(eckohWebhookRequest))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC8");
    }

    @Test
    void testCreatePaymentInvalidPaymentType() throws IOException {
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/createPaymentInvalidPaymentType.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestInvalidPaymentType.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC1");
    }

    @Test
    void testCreatePaymentValidationMissingExample() throws IOException {
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/createPaymentValidationMissing.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse)
                        .build()
        );
        var paymentRequest = PaymentRequest.builder().build();
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC1");
    }

    @Test
    void testPaymentResourceWebhook() throws IOException {
        //Given a payment request
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        var paymentResponse = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        var paymentId = paymentResponse.getPaymentId();

        var eckohWebhookSuccessfulResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/eckohSuccessfulWebhookResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(eckohWebhookSuccessfulResponse))
                        .build()
        );
        //When a webhook is received
        var response = webTestClient
                .post()
                .uri(String.format("/payments/%s/webhook", paymentId))
                .body(BodyInserters.fromFormData(getWebHookData()))
                .exchange();

        //Then valid response
        response.expectStatus().isEqualTo(HttpStatus.OK);

    }

    @Test
    void testPaymentResourceRedirect() throws IOException {
        //Given a payment request
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        var paymentResponse = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        assertNotNull(paymentResponse);
        var paymentId = paymentResponse.getPaymentId();

        //When a redirect is received

        var paymentProviderTransactionResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/paymentProviderTransactionResponse.json"), PaymentProviderTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(paymentProviderTransactionResponse))
                        .build()
        );

        var response = webTestClient
                .get()
                .uri(String.format("/payments/%s/complete?MerchantRef=1234&TxID=4444&AuthorisationCode=5679&CardType=AM&TokenNo=4444333322221111", paymentId))
                .exchange();

        //Then valid response
        response.expectStatus().is2xxSuccessful().expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE);
        response.expectHeader().doesNotExist("X-FRAME-OPTIONS"); // DNRQ-11473 spring security adding default header
    }

    @Test
    void testGetPaymentResource() throws IOException {
        //Given a payment request
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        var paymentResponse = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        assertNotNull(paymentResponse);
        var paymentId = paymentResponse.getPaymentId();

        var eckohWebhookSuccessfulResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/testGetPaymentResource.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(204)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
        );
        var webHookResponse = webTestClient
                .post()
                .uri(String.format("/payments/%s/webhook", paymentId))
                .body(BodyInserters.fromFormData(getWebHookData()))
                .exchange();

        assertNotNull(webHookResponse);

        var getPaymentResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/getPaymentResourceResponse.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(getPaymentResponse))
                        .build()
        );
        var response = webTestClient
                .get()
                .uri(String.format("/payments/%s", paymentId))
                .exchange();

        //Then valid response

        response.expectStatus().is2xxSuccessful();
        response.expectBody()
                .jsonPath("providerResponse.threecResponse.template")
                .isNotEmpty()
                .jsonPath("paymentStatus")
                .isEqualTo("SUCCESS");
    }

    @Test
    void createToken() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        var createTokenSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/tokenCreateResponse.json"), CreateTokenResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML)
                        .body(objectMapper.writeValueAsString(createTokenSuccessResponse))
                        .build()
        );
        var createTokenRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/tokenCreateRequest.json"), CreateTokenRequest.class);
        webTestClient
                .post()
                .uri("tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(createTokenRequest))
                .header("Authorization", "dummyToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.token")
                .isEqualTo("4943056398164344242");
    }

    @Test
    void updateToken() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        var updateTokenSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/tokenUpdateResponse.json"), UpdateTokenResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML)
                        .body(objectMapper.writeValueAsString(updateTokenSuccessResponse))
                        .build()
        );
        var updateTokenRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/tokenUpdateRequest.json"), UpdateTokenRequest.class);
        webTestClient
                .put()
                .uri("tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updateTokenRequest))
                .header("Authorization", "dummyToken")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.token")
                .isEqualTo("4943056398164344242");
    }

    @Test
    void testTransactionalRefund() throws IOException {
        //Given a payment request
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        var paymentIdResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/transactionalRefundPaymentIdResponse.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(paymentIdResponse))
                        .build()
        );
        var paymentIResponseTestClient = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        assertNotNull(paymentIResponseTestClient);
        var paymentId = paymentIResponseTestClient.getPaymentId();

        //When a refund request  is received
        var refundSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/reverseByTransactionIdResponse.json"), ReverseByTransactionIdResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(refundSuccessResponse))
                        .build()
        );

        var response = webTestClient
                .post()
                .uri(String.format("/payments/%s/refund", paymentId))
                .header(apiKeyName, apiKeyValue)
                .exchange();

        //Then valid response
        response.expectStatus().is2xxSuccessful();
    }

    @Test
    void startSiteReconciliation() throws IOException {
        var startReconciliationResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/reconciliationResponse.json"), StartReconciliationResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(startReconciliationResponse))
                        .build()
        );
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(startReconciliationResponse))
                        .build()
        );
        var startReconciliationRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/reconciliationRequest.json"), ReconciliationRequest.class);
        webTestClient
                .post()
                .uri("reconcile")
                .header(apiKeyName, apiKeyValue)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(startReconciliationRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
    }

    @Test
    void testRefundPayment() throws IOException {
        var requestId = UUID.randomUUID().toString();
        var initialiseRefundResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(initialiseRefundResponse))
                        .build()
        );
        var refundRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class);
        refundRequest.setRequestId(requestId);
        webTestClient
                .post()
                .uri("/refunds")
                .header(apiKeyName, apiKeyValue)
                .body(BodyInserters.fromValue(refundRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isNotEmpty()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isEqualTo("e3da0cac-b979-496d-a8ef-129a12f819c9");
    }

    @Test
    void testRefundPaymentWithBookingRef() throws IOException {
        var requestId = UUID.randomUUID().toString();
        var initialiseRefundResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(initialiseRefundResponse))
                        .build()
        );
        var refundRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequestWithoutBookingRef.json"), RefundRequest.class);
        refundRequest.setRequestId(requestId);
        webTestClient
                .post()
                .uri("/refunds")
                .header(apiKeyName, apiKeyValue)
                .body(BodyInserters.fromValue(refundRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isNotEmpty()
                .jsonPath("Response.Params.RequesterTransRefNum")
                .isEqualTo("e3da0cac-b979-496d-a8ef-129a12f819c9");
    }

    @Test
    void testRefundPaymentIdempotency() throws IOException {
        var requestId = UUID.randomUUID().toString();

        //Given Request 1
        var noCardReadTransactionResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(noCardReadTransactionResponse))
                        .build()
        );
        var refundRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class);
        refundRequest.setRequestId(requestId);
        var refundId = webTestClient
                .post()
                .uri("/refunds")
                .header(apiKeyName, apiKeyValue)
                .body(BodyInserters.fromValue(refundRequest))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(RefundResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10))
                .getRefundId();

        var noCardReadTransactionResponseTwo = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/payRequestNoCardReadRefundResponse.json"), NoCardReadTransactionResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(noCardReadTransactionResponseTwo))
                        .build()
        );
        // When Request 2 is sent with the same request id
        var refundRequest2 = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class);
        refundRequest2.setRequestId(requestId);
        var result = webTestClient
                .post()
                .uri("/refunds")
                .header(apiKeyName, apiKeyValue)
                .body(BodyInserters.fromValue(refundRequest2))
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(RefundResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        //Then the refund id should be the same
        assertNotNull(result);
        assertEquals(refundId, result.getRefundId());
    }

    @Test
    void testWeb2PayTimeout_Payments() throws IOException {
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/web2PayTimeout_Payments.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        //.setSocketPolicy(SocketPolicy.NO_RESPONSE)
                        //.setBodyDelay(5000, TimeUnit.MILLISECONDS)
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC10");
    }

    @Test
    void testProviderAccountNotMatchingError() throws IOException {
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/providerAccountNotMatchingError.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        paymentRequest.getPayment().setType("PIBA");
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC2");
    }

    @Test
    void testUnableToParseProviderResponseError() throws IOException {
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/unableToParseProviderResponseError.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC4");
    }

    @Test
    void testProviderError() throws IOException {
        var startReconciliationResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/reconciliationFailureResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(500)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(startReconciliationResponse)
                        .build()
        );
        var startReconciliationRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/reconciliationRequest.json"), ReconciliationRequest.class);
        webTestClient
                .post()
                .uri("reconcile")
                .header(apiKeyName, apiKeyValue)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(startReconciliationRequest))
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("errorCode")
                .isEqualTo("TC6");
    }

    @Test
    void testPaymentResourceNotFoundError() throws IOException {
        var requestId = UUID.randomUUID().toString();
        var validationErrorResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/paymentResourceNotFoundError.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(404)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(validationErrorResponse.replaceAll("%s",requestId))
                        .build()
        );

        //When a webhook is received
        var response = webTestClient
                .post()
                .uri(String.format("/payments/%s/webhook", requestId))
                .body(BodyInserters.fromFormData(getWebHookData()))
                .exchange();

        //Then valid response
        response.expectStatus()
                .is4xxClientError()
                .expectBody()
                .jsonPath("message")
                .isNotEmpty()
                .jsonPath("message")
                .isEqualTo(String.format("Payment with paymentId %s not found.", requestId))
                .jsonPath("errorCode")
                .isEqualTo("TC8");

    }

    @Test
    void testUpdatePaymentWithBookingReference() throws IOException {
        //Given a payment request
        var initialiseSuccessResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/initialiseRoutesIpageResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(initialiseSuccessResponse)
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequest.json"), PaymentRequest.class);
        var requestId = UUID.randomUUID().toString();
        paymentRequest.setRequestId(requestId);
        var paymentResponse = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10));

        assertNotNull(paymentResponse);
        var paymentId = paymentResponse.getPaymentId();

        var eckohWebhookSuccessfulResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/testUpdatePaymentWithBookingReference.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(204)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build()
        );
        var response = webTestClient
                .post()
                .uri(String.format("/payments/%s/webhook", paymentId))
                .body(BodyInserters.fromFormData(getWebHookData()))
                .exchange();

        assertNotNull(response);
        var updateBookingReferenceRequest = UpdateBookingReferenceRequest.builder().paymentId(paymentId).bookingReference(BOOKING_REFERENCE).build();
        //Then expect status
        var updateBookingReferenceResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/updateBookingReferenceResponse.json"), String.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(202)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(updateBookingReferenceResponse))
                        .build()
        );
        webTestClient
                .put()
                .uri("/payments")
                .body(BodyInserters.fromValue(updateBookingReferenceRequest))
                .exchange()
                .expectStatus()
                .isAccepted();
    }

    @Test
    void testUpdatePaymentWithBookingReference_ExpectBadRequest() {
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(400)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("")
                        .build()
        );
        var paymentId = UUID.randomUUID().toString();
        webTestClient
                .put()
                .uri("/payments")
                .body(BodyInserters.fromValue(UpdateBookingReferenceRequest.builder().paymentId(paymentId).build()))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    private LinkedMultiValueMap<String, String> getWebHookData() {
        var multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.put("TxState", List.of("CQ"));
        multiValueMap.put("TxID", List.of("63d325c1-fb62-4a12-b368-a1b1472f802a"));
        multiValueMap.put("ref", List.of("1211114"));
        multiValueMap.put("3DSIndicator", List.of("3"));
        multiValueMap.put("Amount", List.of("1000"));
        multiValueMap.put("AuthorisationCode", List.of("110920"));
        multiValueMap.put("CardNumberFirst6", List.of("424242"));
        multiValueMap.put("CardType", List.of("VS"));
        multiValueMap.put("CardTypeName", List.of("VISA"));
        multiValueMap.put("CurrencyCode", List.of("GBP"));
        multiValueMap.put("CardExpiry", List.of("2405"));
        multiValueMap.put("ReturnCode", List.of("0000"));
        multiValueMap.put("SCATransRef", List.of("V0202105131109203UGA"));
        multiValueMap.put("card_pan_last4digits", List.of("4242"));
        multiValueMap.put("TokenNo", List.of("4943056398164344242"));
        multiValueMap.put("fraud_check_decision", List.of("ACCEPT"));
        multiValueMap.put("fraud_check_result", List.of("1234"));
        multiValueMap.put("fraud_check_result_reason", List.of("APPROVED"));
        return multiValueMap;
    }
}
