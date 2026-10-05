package uk.co.whitbread.payments.routes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payments.model.PaymentRequest;
import uk.co.whitbread.payments.model.PaymentResponse;
import uk.co.whitbread.payments.model.ReconciliationRequest;
import uk.co.whitbread.payments.model.RefundRequest;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

class SecuredRoutesTest {

    private static MockWebServer mockWebServer;
    private ObjectMapper objectMapper;
    private WebTestClient webTestClient;

    @Getter
    private static final int PORT = 8080;

    @AfterEach
    void afterEach() {
        mockWebServer.close();
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(PORT);

        var jsonMapper = JsonMapper.builder()
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .findAndAddModules()
                .build();

        webTestClient = WebTestClient.bindToServer()
                .baseUrl(String.format("http://localhost:%s", mockWebServer.getPort()))
                .responseTimeout(Duration.ofSeconds(10))
                .codecs(configurer -> {
                    configurer.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    configurer.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();
        objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testSiteReconciliationWhenAPIKeyNotPassed() throws IOException {
        var startReconciliationRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/reconciliationRequest.json"), ReconciliationRequest.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(401)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("")
                        .build()
        );
        webTestClient
                .post()
                .uri("reconcile")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(startReconciliationRequest))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testTransactionalRefundWhenAPIKeyNotPassed() throws IOException {
        //Given a payment request
        var paymentIdResponse = objectMapper.readValue(new File("src/test/resources/stubs/3c/responses/transactionalRefundPaymentIdResponse.json"), PaymentResponse.class);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(objectMapper.writeValueAsString(paymentIdResponse))
                        .build()
        );
        var paymentRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createPaymentRequestEurMoto.json"), PaymentRequest.class);
        paymentRequest.setRequestId(UUID.randomUUID().toString());
        var paymentId = webTestClient
                .post()
                .uri("/payments")
                .body(BodyInserters.fromValue(paymentRequest))
                .exchange()
                .returnResult(PaymentResponse.class)
                .getResponseBody()
                .blockFirst(Duration.ofSeconds(10))
                .getPaymentId();

        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(401)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("")
                        .build()
        );
        webTestClient
                .post()
                .uri(String.format("/payments/%s/refund", paymentId))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void testRefundPaymentWhenAPIKeyNotPassed() throws IOException {
        var requestId = UUID.randomUUID().toString();
        var refundRequest = objectMapper.readValue(new File("src/test/resources/stubs/3c/requests/createRefundRequest.json"), RefundRequest.class);
        refundRequest.setRequestId(requestId);
        mockWebServer.enqueue(
                new MockResponse.Builder()
                        .code(401)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body("")
                        .build()
        );
        webTestClient
                .post()
                .uri("/refunds")
                .body(BodyInserters.fromValue(refundRequest))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }
}
