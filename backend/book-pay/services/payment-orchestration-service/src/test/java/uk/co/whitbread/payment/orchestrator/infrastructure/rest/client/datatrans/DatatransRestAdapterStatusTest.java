package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.datatrans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransProperties;

class DatatransRestAdapterStatusTest {

  private static final String MERCHANT_ID = "deWB-HOTEL-001";
  private static final String MERCHANT_PASSWORD = "status-test-merchant-password";
  private static final String TRANSACTION_ID = "190410112056083383";
  private static final String CARD_ALIAS = "AAABcH0Bq92s3kgAESIAAbGj5NIs";
  private static final String CARD_MASKED = "424242xxxxxx4242";

  private MockWebServer mockWebServer;
  private DatatransRestAdapter adapter;
  private ListAppender<ILoggingEvent> logAppender;
  private Logger adapterLogger;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    DatatransProperties properties = new DatatransProperties();
    properties.setBaseUrl(mockWebServer.url("/").toString());
    properties.setMerchantPassword(MERCHANT_PASSWORD);
    properties.setConnectTimeout(Duration.ofSeconds(5));
    properties.setReadTimeout(Duration.ofSeconds(5));

    adapter = new DatatransRestAdapter(createRestClient(Duration.ofSeconds(5)), properties);

    adapterLogger = (Logger) LoggerFactory.getLogger(DatatransRestAdapter.class);
    logAppender = new ListAppender<>();
    logAppender.start();
    adapterLogger.addAppender(logAppender);
  }

  @AfterEach
  void tearDown() throws IOException {
    adapterLogger.detachAppender(logAppender);
    mockWebServer.shutdown();
  }

  @Test
  void sendsGetRequestToTransactionStatusPathWithBasicAuth() throws InterruptedException {
    mockWebServer.enqueue(jsonResponse(200, """
        {
          "transactionId": "190410112056083383",
          "status": "initialized"
        }
        """));

    adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID);

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
    assertThat(recordedRequest.getPath()).isEqualTo("/v2/transactions/190410112056083383");
    assertThat(recordedRequest.getHeader("Authorization"))
        .isEqualTo("Basic " + encodedCredentials());
  }

  @ParameterizedTest
  @ValueSource(strings = {"authorized", "settled"})
  void mapsCompleteAuthorizedOrSettledResponseIncludingCardAlias(String status) {
    mockWebServer.enqueue(jsonResponse(200, completeStatusResponse(status)));

    DatatransTransactionStatus result = adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID);

    assertThat(result.transactionId()).isEqualTo(TRANSACTION_ID);
    assertThat(result.status()).isEqualTo(status);
    assertThat(result.currency()).isEqualTo("GBP");
    assertThat(result.authorizedAmount()).isEqualTo(8600);
    assertThat(result.acquirerAuthorizationCode()).isEqualTo("160600");
    assertThat(result.paymentMethod()).isEqualTo("VIS");
    assertThat(result.card()).isNotNull();
    assertThat(result.card().alias()).isEqualTo(CARD_ALIAS);
    assertThat(result.card().masked()).isEqualTo(CARD_MASKED);
    assertThat(result.card().expiryMonth()).isEqualTo("12");
    assertThat(result.card().expiryYear()).isEqualTo("28");
  }

  @Test
  void throwsGatewayExceptionWhenResponseBodyIsAbsent() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(204));

    assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
        .isInstanceOf(DatatransGatewayException.class)
        .hasMessageContaining("absent");
  }

  @Test
  void throwsGatewayExceptionWhenResponseStatusIsBlank() {
    mockWebServer.enqueue(jsonResponse(200, """
        {
          "transactionId": "190410112056083383",
          "status": "   "
        }
        """));

    assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
        .isInstanceOf(DatatransGatewayException.class)
        .hasMessageContaining("blank status");
  }

  @Test
  void throwsTransactionNotFoundExceptionForNotFoundResponse() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(404));

    assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
        .isInstanceOf(TransactionNotFoundException.class)
        .hasMessageContaining("not found");
  }

  @ParameterizedTest
  @ValueSource(ints = {400, 422, 500, 503})
  void throwsGatewayExceptionContainingHttpStatusForOtherErrorResponses(int statusCode) {
    mockWebServer.enqueue(new MockResponse().setResponseCode(statusCode));

    assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
        .isInstanceOf(DatatransGatewayException.class)
        .hasMessageContaining(String.valueOf(statusCode));
  }

  @Test
  void throwsServiceUnavailableExceptionWhenConnectionIsRefused() throws IOException {
    mockWebServer.shutdown();

    assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
        .isInstanceOf(ServiceUnavailableException.class);
  }

  @Test
  void throwsServiceUnavailableExceptionWhenReadTimesOut() {
    adapter = new DatatransRestAdapter(createRestClient(Duration.ofMillis(50)),
        propertiesForCurrentServer());
    mockWebServer.enqueue(new MockResponse()
        .setBodyDelay(500, TimeUnit.MILLISECONDS)
        .setBody(completeStatusResponse("authorized")));

    assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
        .isInstanceOf(ServiceUnavailableException.class);
  }

  @Test
  void doesNotLogMerchantPasswordOrCardObject() {
    mockWebServer.enqueue(jsonResponse(200, completeStatusResponse("authorized")));

    adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID);

    String logs = logAppender.list.stream()
        .map(ILoggingEvent::getFormattedMessage)
        .reduce("", (all, message) -> all + message);
    assertThat(logs).doesNotContain(MERCHANT_PASSWORD);
    assertThat(logs).doesNotContain(CARD_ALIAS);
    assertThat(logs).doesNotContain(CARD_MASKED);
    assertThat(logs).doesNotContain("\"card\"");
  }

  private DatatransProperties propertiesForCurrentServer() {
    DatatransProperties properties = new DatatransProperties();
    properties.setBaseUrl(mockWebServer.url("/").toString());
    properties.setMerchantPassword(MERCHANT_PASSWORD);
    properties.setConnectTimeout(Duration.ofSeconds(5));
    properties.setReadTimeout(Duration.ofMillis(50));
    return properties;
  }

  private RestClient createRestClient(Duration readTimeout) {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(Duration.ofSeconds(5));
    requestFactory.setReadTimeout(readTimeout);
    return RestClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .requestFactory(requestFactory)
        .build();
  }

  private String encodedCredentials() {
    return Base64.getEncoder().encodeToString(
        (MERCHANT_ID + ":" + MERCHANT_PASSWORD).getBytes(StandardCharsets.UTF_8));
  }

  private MockResponse jsonResponse(int statusCode, String body) {
    return new MockResponse()
        .setResponseCode(statusCode)
        .setHeader("Content-Type", "application/json")
        .setBody(body);
  }

  private String completeStatusResponse(String status) {
    return """
        {
          "transactionId": "190410112056083383",
          "status": "%s",
          "currency": "GBP",
          "authorizedAmount": 8600,
          "acquirerAuthorizationCode": "160600",
          "paymentMethod": "VIS",
          "card": {
            "alias": "AAABcH0Bq92s3kgAESIAAbGj5NIs",
            "masked": "424242xxxxxx4242",
            "expiryMonth": "12",
            "expiryYear": "28",
            "info": {
              "brand": "VISA CREDIT",
              "type": "credit",
              "country": "GB"
            }
          },
          "attempts": [
            {
              "status": "%s",
              "amount": 8600,
              "acquirerAuthorizationCode": "160600"
            }
          ]
        }
        """.formatted(status, status);
  }
}
