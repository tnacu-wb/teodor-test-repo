package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.datatrans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransProperties;

class DatatransRestAdapterTest {

  private MockWebServer mockWebServer;
  private DatatransRestAdapter adapter;
  private DatatransProperties properties;

  private static final String MERCHANT_ID = "deWB-HOTEL-001";
  private static final String MERCHANT_PASSWORD = "FAKE_MERCHANT_PASSWORD";

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    properties = new DatatransProperties();
    properties.setBaseUrl(mockWebServer.url("/").toString());
    properties.setMerchantPassword(MERCHANT_PASSWORD);
    
    properties.setConnectTimeout(Duration.ofSeconds(5));
    properties.setReadTimeout(Duration.ofSeconds(10));

    RestClient restClient = RestClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    adapter = new DatatransRestAdapter(restClient, properties);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Nested
  class SuccessfulResponse {

    @Test
    void extractsTransactionIdFromV2Response() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"019ff5ab-577c-7c90-8b46-1b818a4f37a2\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID,
          "POST");

      String transactionId = adapter.initSecureFields(request);

      assertThat(transactionId).isEqualTo("019ff5ab-577c-7c90-8b46-1b818a4f37a2");

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      assertThat(recordedRequest.getPath()).isEqualTo("/v2/transactions/secure-fields");
      assertThat(recordedRequest.getMethod()).isEqualTo("POST");

      String authHeader = recordedRequest.getHeader("Authorization");
      assertThat(authHeader).isNotNull();
      String expectedCredentials = MERCHANT_ID + ":" + MERCHANT_PASSWORD;
      String expectedEncoded = Base64.getEncoder()
          .encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));
      assertThat(authHeader).isEqualTo("Basic " + expectedEncoded);

      String body = recordedRequest.getBody().readUtf8();
      assertThat(body).contains("\"amount\":8600");
      assertThat(body).contains("\"currency\":\"GBP\"");
      assertThat(body).contains("\"returnUrl\":\"https://www.premierinn.com/payments/3ds-return\"");
      assertThat(body).contains("\"returnMethod\":\"POST\"");
    }

    @Test
    void sendsCorrectRequestBody() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"019ff5ab-1234-7a00-9000-abcdef012345\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          4999L, "EUR", "https://example.com/return", MERCHANT_ID, "POST");

      adapter.initSecureFields(request);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String body = recordedRequest.getBody().readUtf8();
      assertThat(body).contains("\"amount\":4999");
      assertThat(body).contains("\"currency\":\"EUR\"");
      assertThat(body).contains("\"returnUrl\":\"https://example.com/return\"");
      assertThat(body).contains("\"returnMethod\":\"POST\"");
    }

    @Test
    void throwsGatewayExceptionForEmptyResponse() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": null}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("empty response");
    }
  }

  @Nested
  class ErrorResponses {

    @Test
    void throws_gatewayException_on4xxResponse() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(400)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Bad Request\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Datatrans returned");
    }

    @Test
    void throws_datatransAuthenticationException_on403Forbidden() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(403)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Forbidden\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      // A 403 means our merchant is not permitted to do this, not that the gateway failed
      // generically — it is worth a distinct, non-retryable type so ops sees the real cause.
      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(DatatransAuthenticationException.class)
          .isNotInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("rejected merchant credentials");
    }

    @Test
    void throws_gatewayException_on5xxResponse() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Datatrans returned");
    }

    @Test
    void throws_gatewayException_on502BadGateway() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(502)
          .setHeader("Content-Type", "application/json")
          .setBody(""));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Datatrans returned");
    }
  }

  @Nested
  class ConnectionErrors {

    @Test
    void throws_serviceUnavailableException_onConnectionRefused() throws IOException {
      mockWebServer.shutdown();

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Datatrans is unreachable");
    }
  }

  @Nested
  class BasicAuthHeader {

    @Test
    void setsBasicAuthHeaderCorrectly() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-auth-test\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://www.premierinn.com/payments/3ds-return", MERCHANT_ID, "POST");

      adapter.initSecureFields(request);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String authHeader = recordedRequest.getHeader("Authorization");
      assertThat(authHeader).isNotNull();
      assertThat(authHeader).startsWith("Basic ");

      String expectedCredentials = MERCHANT_ID + ":" + MERCHANT_PASSWORD;
      String expectedEncoded = Base64.getEncoder()
          .encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));
      assertThat(authHeader).isEqualTo("Basic " + expectedEncoded);
    }

    @Test
    void usesCredentialsFromProperties() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-creds-test\"}"));

      DatatransSecureFieldsRequest request = new DatatransSecureFieldsRequest(
          1000L, "JPY", "https://example.com/return", MERCHANT_ID, "POST");

      adapter.initSecureFields(request);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String authHeader = recordedRequest.getHeader("Authorization");

      // Decode and verify the credentials match properties
      String base64Part = authHeader.substring("Basic ".length());
      String decoded = new String(
          Base64.getDecoder().decode(base64Part), StandardCharsets.UTF_8);
      assertThat(decoded).isEqualTo(MERCHANT_ID + ":" + MERCHANT_PASSWORD);
    }
  }

  // ==================== authorizeTransaction success tests ====================

  @Nested
  class AuthorizeSuccessResponse {

    private static final String TRANSACTION_ID = "019ff5ab-577c-7c90-8b46-1b818a4f37a2";
    private static final String REFNO = "PI-AQN-147756bb";
    private static final long AMOUNT = 8600L;

    private static final String AUTHORIZE_RESPONSE_BODY = """
        {
          "acquirerAuthorizationCode": "131544",
          "card": {
            "alias": "424242SKMPRI4242",
            "masked": "424242xxxxxx4242",
            "expiryMonth": "06",
            "expiryYear": "28"
          },
          "transactionId": "019ff5ab-577c-7c90-8b46-1b818a4f37a2",
          "status": "authorized",
          "paymentMethod": "VIS"
        }
        """;

    @Test
    void sendsRequestToV2AuthorizePath() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      assertThat(recordedRequest.getPath())
          .isEqualTo("/v2/transactions/" + TRANSACTION_ID + "/authorize");
      assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    void sendsRefnoAndAmountInRequestBody() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String body = recordedRequest.getBody().readUtf8();
      assertThat(body).contains("\"refno\":\"" + REFNO + "\"");
      assertThat(body).contains("\"amount\":" + AMOUNT);
    }

    @Test
    void sendsBasicAuthHeader() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String authHeader = recordedRequest.getHeader("Authorization");
      assertThat(authHeader).isNotNull();
      assertThat(authHeader).startsWith("Basic ");

      String expectedCredentials = MERCHANT_ID + ":" + MERCHANT_PASSWORD;
      String expectedEncoded = Base64.getEncoder()
          .encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));
      assertThat(authHeader).isEqualTo("Basic " + expectedEncoded);
    }

    @Test
    void extractsAcquirerAuthorizationCode() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      assertThat(response.acquirerAuthorizationCode()).isEqualTo("131544");
    }

    @Test
    void extractsCardAlias() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      assertThat(response.card()).isNotNull();
      assertThat(response.card().alias()).isEqualTo("424242SKMPRI4242");
    }

    @Test
    void extractsCardMaskedAndExpiry() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      assertThat(response.card().masked()).isEqualTo("424242xxxxxx4242");
      assertThat(response.card().expiryMonth()).isEqualTo("06");
      assertThat(response.card().expiryYear()).isEqualTo("28");
    }

    @Test
    void extractsPaymentMethod() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(AUTHORIZE_RESPONSE_BODY));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, REFNO, AMOUNT, MERCHANT_ID);

      assertThat(response.paymentMethod()).isEqualTo("VIS");
    }
  }

  // ==================== initMobileSdk tests ====================

  @Nested
  class MobileSdkSuccessfulResponse {

    @Test
    void returnsTransactionIdOn201Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"2d49fde3-3f03-4b45-8b3e-a5c1e2f7d8e9\"}"));

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID);

      assertThat(adapter.initMobileSdk(request))
          .isEqualTo("2d49fde3-3f03-4b45-8b3e-a5c1e2f7d8e9");
    }

    @Test
    void sendsCorrectRequestBody() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-body-test\"}"));

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID);

      adapter.initMobileSdk(request);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      assertThat(recordedRequest.getPath()).isEqualTo("/v2/transactions");
      assertThat(recordedRequest.getMethod()).isEqualTo("POST");

      String body = recordedRequest.getBody().readUtf8();
      assertThat(body).contains("\"amount\":8600");
      assertThat(body).contains("\"currency\":\"GBP\"");
      assertThat(body).contains("\"refno\":\"PI-basket-123\"");
      assertThat(body).contains("\"paymentMethods\":[\"VIS\",\"ECA\"]");
    }

    @Test
    void sendsWebhookUrlWhenProvided() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-webhook-test\"}"));

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID,
          "https://host/api/payments/webhooks/datatrans?basketId=basket-123");

      adapter.initMobileSdk(request);

      String body = mockWebServer.takeRequest().getBody().readUtf8();
      assertThat(body).contains(
          "\"webhook\":{\"url\":\"https://host/api/payments/webhooks/datatrans"
              + "?basketId=basket-123\"}");
    }

    @Test
    void omitsWebhookWhenUrlIsNullOrBlank() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-no-webhook\"}"));
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-no-webhook\"}"));

      adapter.initMobileSdk(new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS"), MERCHANT_ID, null));
      adapter.initMobileSdk(new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS"), MERCHANT_ID, "  "));

      assertThat(mockWebServer.takeRequest().getBody().readUtf8())
          .doesNotContain("webhook");
      assertThat(mockWebServer.takeRequest().getBody().readUtf8())
          .doesNotContain("webhook");
    }
  }

  @Nested
  class MobileSdkErrorResponses {

    @Test
    void throwsDatatransGatewayExceptionOn400Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(400)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Bad Request\"}"));

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID);

      assertThatThrownBy(() -> adapter.initMobileSdk(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Datatrans v2 returned");
    }

    @Test
    void throwsDatatransGatewayExceptionOn500Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID);

      assertThatThrownBy(() -> adapter.initMobileSdk(request))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Datatrans v2 returned");
    }
  }

  @Nested
  class MobileSdkConnectionErrors {

    @Test
    void throwsServiceUnavailableExceptionOnConnectionRefused() throws IOException {
      mockWebServer.shutdown();

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID);

      assertThatThrownBy(() -> adapter.initMobileSdk(request))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Datatrans is unreachable");
    }
  }

  @Nested
  class MobileSdkBasicAuthHeader {

    @Test
    void sendsBasicAuthHeaderWithMerchantCredentials() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(201)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"transactionId\": \"txn-auth-mobile\"}"));

      DatatransMobileSdkRequest request = new DatatransMobileSdkRequest(
          8600L, "GBP", "PI-basket-123", List.of("VIS", "ECA"), MERCHANT_ID);

      adapter.initMobileSdk(request);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String authHeader = recordedRequest.getHeader("Authorization");
      assertThat(authHeader).isNotNull();
      assertThat(authHeader).startsWith("Basic ");

      String expectedCredentials = MERCHANT_ID + ":" + MERCHANT_PASSWORD;
      String expectedEncoded = Base64.getEncoder()
          .encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));
      assertThat(authHeader).isEqualTo("Basic " + expectedEncoded);
    }
  }

  // ==================== cancelTransaction tests ====================

  @Nested
  class CancelTransactionSuccess {

    private static final String TRANSACTION_ID = "019ff5ab-577c-7c90-8b46-1b818a4f37a2";

    @Test
    void sendsPostToCancelEndpoint() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse().setResponseCode(204));

      adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      assertThat(recordedRequest.getPath())
          .isEqualTo("/v2/transactions/" + TRANSACTION_ID + "/cancel");
      assertThat(recordedRequest.getMethod()).isEqualTo("POST");
    }

    @Test
    void sendsEmptyRequestBody() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse().setResponseCode(204));

      adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      assertThat(recordedRequest.getBody().size()).isZero();
    }

    @Test
    void sendsBasicAuthHeaderWithMerchantCredentials() throws InterruptedException {
      mockWebServer.enqueue(new MockResponse().setResponseCode(204));

      adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID);

      RecordedRequest recordedRequest = mockWebServer.takeRequest();
      String authHeader = recordedRequest.getHeader("Authorization");
      assertThat(authHeader).isNotNull();

      String expectedCredentials = MERCHANT_ID + ":" + MERCHANT_PASSWORD;
      String expectedEncoded = Base64.getEncoder()
          .encodeToString(expectedCredentials.getBytes(StandardCharsets.UTF_8));
      assertThat(authHeader).isEqualTo("Basic " + expectedEncoded);
    }

    @Test
    void completesSuccessfullyOn204Response() {
      mockWebServer.enqueue(new MockResponse().setResponseCode(204));

      // Should not throw any exception
      adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID);
    }
  }

  @Nested
  class CancelTransactionErrorResponses {

    private static final String TRANSACTION_ID = "019ff5ab-577c-7c90-8b46-1b818a4f37a2";

    @Test
    void throwsTransactionNotFoundExceptionOn404() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(404)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Transaction not found\"}"));

      assertThatThrownBy(() -> adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID))
          .isInstanceOf(TransactionNotFoundException.class)
          .hasMessageContaining("Transaction not found or already settled");
    }

    @Test
    void throwsDatatransGatewayExceptionOn400() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(400)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Transaction cannot be cancelled\"}"));

      assertThatThrownBy(() -> adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Cancellation failed");
    }

    @Test
    void throwsDatatransGatewayExceptionOn500() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Cancellation failed");
    }
  }

  @Nested
  class CancelTransactionConnectionErrors {

    private static final String TRANSACTION_ID = "019ff5ab-577c-7c90-8b46-1b818a4f37a2";

    @Test
    void throwsServiceUnavailableExceptionOnConnectionRefused() throws IOException {
      mockWebServer.shutdown();

      assertThatThrownBy(() -> adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Datatrans is unreachable");
    }
  }
}
