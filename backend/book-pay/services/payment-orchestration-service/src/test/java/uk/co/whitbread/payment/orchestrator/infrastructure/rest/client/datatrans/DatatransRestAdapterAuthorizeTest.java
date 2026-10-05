package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.datatrans;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransGatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionMismatchException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransProperties;

class DatatransRestAdapterAuthorizeTest {

  private MockWebServer mockWebServer;
  private DatatransRestAdapter adapter;
  private DatatransProperties properties;
  private ListAppender<ILoggingEvent> logAppender;
  private Logger adapterLogger;

  private static final String MERCHANT_ID = "FAKE_MERCHANT_ID";
  private static final String MERCHANT_PASSWORD = "FAKE_MERCHANT_PASSWORD";
  private static final String TRANSACTION_ID = "250610143022001234";
  private static final String REFNO = "ARH1234567";

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    properties = new DatatransProperties();
    properties.setBaseUrl(mockWebServer.url("/").toString());
    properties.setMerchantPassword(MERCHANT_PASSWORD);
    properties.setConnectTimeout(Duration.ofSeconds(5));
    properties.setReadTimeout(Duration.ofSeconds(5));

    RestClient restClient = RestClient.builder()
        .baseUrl(mockWebServer.url("/").toString())
        .build();

    adapter = new DatatransRestAdapter(restClient, properties);

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

  @Nested
  class AuthorizeTransactionSuccess {

    @Test
    void returns_noException_on200OkResponse() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody("""
              {
                "transactionId": "250610143022001234",
                "status": "authorized",
                "acquirerAuthorizationCode": "123456",
                "card": {
                  "alias": "AAABcH0Bq92s3kgAESIAAbGj5NIs",
                  "masked": "424242xxxxxx4242",
                  "expiryMonth": "12",
                  "expiryYear": "25"
                }
              }
              """));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, "test-basket", 8600L, MERCHANT_ID);

      assertThat(response).isNotNull();
      assertThat(response.transactionId()).isEqualTo("250610143022001234");
      assertThat(response.status()).isEqualTo("authorized");
      assertThat(response.acquirerAuthorizationCode()).isEqualTo("123456");
      assertThat(response.card()).isNotNull();
      assertThat(response.card().alias()).isEqualTo("AAABcH0Bq92s3kgAESIAAbGj5NIs");
      assertThat(response.card().masked()).isEqualTo("424242xxxxxx4242");
    }
  }

  @Nested
  class AuthorizeTransactionErrors {

    @Test
    void throws_transactionNotFoundException_on404Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(404)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Not Found\"}"));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, "test-basket", 8600L, MERCHANT_ID))
          .isInstanceOf(TransactionNotFoundException.class)
          .hasMessageContaining("not found");
    }

    @Test
    void throws_threeDsAuthenticationFailedException_on409Response_whenNothingWasAuthorized() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(409)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": {\"code\": \"3D_AUTHENTICATION_FAILED\"}}"));
      mockWebServer.enqueue(statusResponse("initialized", 8600, REFNO));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(ThreeDsAuthenticationFailedException.class)
          .hasMessageContaining("3-D Secure authentication failed");
    }

    @Test
    void throws_datatransGatewayException_on500Response() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, "test-basket", 8600L, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Datatrans returned");
    }
  }

  @Nested
  class AuthorizeTransactionConnectionErrors {

    @Test
    void throws_serviceUnavailableException_onConnectionRefused() throws IOException {
      mockWebServer.shutdown();

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, "test-basket", 8600L, MERCHANT_ID))
          .isInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("Datatrans is unreachable");
    }
  }

  @Nested
  class SettleTransactionSuccess {

    @Test
    void returns_noException_on200OkResponse() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(200)
          .setHeader("Content-Type", "application/json")
          .setBody(""));

      assertThatCode(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", "test-basket", MERCHANT_ID))
          .doesNotThrowAnyException();
    }
  }

  @Nested
  class SettleTransactionErrors {

    @Test
    void throws_datatransGatewayException_on4xxResponse_whenNothingWasSettled() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(400)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": {\"code\": \"INVALID_PROPERTY\"}}"));
      mockWebServer.enqueue(statusResponse("authorized", 8600, REFNO));

      assertThatThrownBy(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Settlement failed");
    }

    @Test
    void throws_datatransGatewayException_on5xxResponse() {
      mockWebServer.enqueue(new MockResponse()
          .setResponseCode(500)
          .setHeader("Content-Type", "application/json")
          .setBody("{\"error\": \"Internal Server Error\"}"));

      assertThatThrownBy(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", "test-basket", MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Settlement failed");
    }
  }

  /**
   * Datatrans answers {@code 409} on authorize both for a genuine 3-D Secure failure and for a
   * transaction it has already authorized — which is what a Temporal retry of an attempt whose
   * response was lost looks like. These tests pin the rule that the transaction status, not the
   * status code, decides which of the two happened.
   */
  @Nested
  class AuthorizeConflictRecovery {

    @Test
    void returns_authorizedResult_when409AndTransactionIsAlreadyAuthorized() {
      mockWebServer.enqueue(conflictResponse(409, "TRANSACTION_ALREADY_AUTHORIZED"));
      mockWebServer.enqueue(statusResponse("authorized", 8600, REFNO));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID);

      assertThat(response).isNotNull();
      assertThat(response.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(response.status()).isEqualTo("authorized");
      assertThat(response.acquirerAuthorizationCode()).isEqualTo("160600");
      assertThat(response.paymentMethod()).isEqualTo("VIS");
      assertThat(response.card()).isNotNull();
      assertThat(response.card().alias()).isEqualTo("AAABcH0Bq92s3kgAESIAAbGj5NIs");
    }

    @Test
    void returns_authorizedResult_when409AndTransactionIsAlreadySettled() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(statusResponse("settled", 8600, REFNO));

      DatatransAuthorizeResponse response =
          adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID);

      assertThat(response.status()).isEqualTo("authorized");
      assertThat(response.transactionId()).isEqualTo(TRANSACTION_ID);
    }

    @Test
    void sends_statusRequestForTheSameTransaction_on409() throws InterruptedException {
      mockWebServer.enqueue(conflictResponse(409, "TRANSACTION_ALREADY_AUTHORIZED"));
      mockWebServer.enqueue(statusResponse("authorized", 8600, REFNO));

      adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID);

      RecordedRequest authorizeRequest = mockWebServer.takeRequest();
      RecordedRequest statusRequest = mockWebServer.takeRequest();
      assertThat(authorizeRequest.getPath())
          .isEqualTo("/v2/transactions/" + TRANSACTION_ID + "/authorize");
      assertThat(statusRequest.getMethod()).isEqualTo("GET");
      assertThat(statusRequest.getPath()).isEqualTo("/v2/transactions/" + TRANSACTION_ID);
    }

    @Test
    void throws_retryableGatewayException_when409AndStatusRequestFails() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(new MockResponse().setResponseCode(500));

      // Ambiguity must never become a terminal payment failure — the retryable gateway
      // exception is what lets Temporal ask again rather than reporting a 3-D Secure failure
      // for money that may already be held.
      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .isNotInstanceOf(ThreeDsAuthenticationFailedException.class)
          .hasMessageContaining("500");
    }

    @Test
    void throws_retryableGatewayException_when409AndStatusConnectionIsDropped() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(new MockResponse()
          .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .isNotInstanceOf(ThreeDsAuthenticationFailedException.class);
    }

    @Test
    void throws_transactionMismatchException_when409AndAuthorizedAmountDiffers() {
      mockWebServer.enqueue(conflictResponse(409, "TRANSACTION_ALREADY_AUTHORIZED"));
      mockWebServer.enqueue(statusResponse("authorized", 9900, REFNO));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(TransactionMismatchException.class)
          .hasMessageContaining("authorized amount")
          .hasMessageContaining("9900");
    }

    @Test
    void throws_transactionMismatchException_when409AndAuthorizedAmountIsAbsent() {
      mockWebServer.enqueue(conflictResponse(409, "TRANSACTION_ALREADY_AUTHORIZED"));
      mockWebServer.enqueue(statusResponse("authorized", null, REFNO));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(TransactionMismatchException.class)
          .hasMessageContaining("authorized amount");
    }

    @Test
    void throws_transactionMismatchException_when409AndRefnoBelongsToAnotherBooking() {
      mockWebServer.enqueue(conflictResponse(409, "TRANSACTION_ALREADY_AUTHORIZED"));
      mockWebServer.enqueue(statusResponse("authorized", 8600, "ARH7654321"));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(TransactionMismatchException.class)
          .hasMessageContaining("refno");
    }
  }

  /**
   * Settling a transaction Datatrans has already settled is what a retry of a lost settle
   * response looks like. The rejection is resolved against the transaction status so the
   * retry converges on one capture instead of failing the booking.
   */
  @Nested
  class SettleConflictRecovery {

    @Test
    void returns_noException_when409AndTransactionIsAlreadySettled() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(statusResponse("settled", 8600, REFNO));

      assertThatCode(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .doesNotThrowAnyException();
    }

    @Test
    void returns_noException_when400AndTransactionIsAlreadySettled() {
      mockWebServer.enqueue(conflictResponse(400, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(statusResponse("settled", 8600, REFNO));

      assertThatCode(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .doesNotThrowAnyException();
    }

    @Test
    void throws_datatransGatewayException_when409AndTransactionIsNotSettled() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(statusResponse("authorized", 8600, REFNO));

      assertThatThrownBy(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Settlement failed")
          .hasMessageContaining("authorized");
    }

    @Test
    void throws_retryableGatewayException_when409AndStatusRequestFails() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(new MockResponse().setResponseCode(503));

      assertThatThrownBy(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .isInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("503");
    }

    @Test
    void throws_transactionMismatchException_when409AndSettledAmountDiffers() {
      mockWebServer.enqueue(conflictResponse(409, "INVALID_TRANSACTION_STATUS"));
      mockWebServer.enqueue(statusResponse("settled", 9900, REFNO));

      assertThatThrownBy(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .isInstanceOf(TransactionMismatchException.class)
          .hasMessageContaining("authorized amount");
    }
  }

  /**
   * A {@code 401} or {@code 403} from Datatrans means our own merchant credentials or
   * permissions are wrong — no card was ever presented to an issuer and no money moved. These
   * tests pin that it is never reported as a decline, on any call.
   */
  @Nested
  class MerchantCredentialsRejected {

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void throws_datatransAuthenticationException_whenAuthorizeCredentialsAreRejected(int status) {
      mockWebServer.enqueue(credentialsResponse(status));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(DatatransAuthenticationException.class)
          .isNotInstanceOf(ThreeDsAuthenticationFailedException.class)
          .hasMessageContaining("rejected merchant credentials")
          .hasMessageContaining("authorize");
    }

    @Test
    void reports_nothingAboutTheCardOrTheIssuer_whenAuthorizeCredentialsAreRejected() {
      mockWebServer.enqueue(credentialsResponse(401));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .hasMessageNotContainingAny("card", "Card", "issuer", "declined", "Declined");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void throws_datatransAuthenticationException_whenSettleCredentialsAreRejected(int status) {
      mockWebServer.enqueue(credentialsResponse(status));

      assertThatThrownBy(() -> adapter.settleTransaction(TRANSACTION_ID, 8600L, "GBP", REFNO, MERCHANT_ID))
          .isInstanceOf(DatatransAuthenticationException.class)
          .hasMessageContaining("settle");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void throws_datatransAuthenticationException_whenCancelCredentialsAreRejected(int status) {
      mockWebServer.enqueue(credentialsResponse(status));

      assertThatThrownBy(() -> adapter.cancelTransaction(TRANSACTION_ID, MERCHANT_ID))
          .isInstanceOf(DatatransAuthenticationException.class)
          .hasMessageContaining("cancel");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void throws_datatransAuthenticationException_whenStatusCredentialsAreRejected(int status) {
      mockWebServer.enqueue(credentialsResponse(status));

      assertThatThrownBy(() -> adapter.getTransactionStatus(TRANSACTION_ID, MERCHANT_ID))
          .isInstanceOf(DatatransAuthenticationException.class)
          .isNotInstanceOf(ServiceUnavailableException.class)
          .hasMessageContaining("status");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void throws_datatransAuthenticationException_whenSecureFieldsInitCredentialsAreRejected(
        int status) {
      mockWebServer.enqueue(credentialsResponse(status));

      var request = new DatatransSecureFieldsRequest(
          8600L, "GBP", "https://premierinn.com/return", MERCHANT_ID, "POST");

      assertThatThrownBy(() -> adapter.initSecureFields(request))
          .isInstanceOf(DatatransAuthenticationException.class)
          .isNotInstanceOf(DatatransGatewayException.class)
          .hasMessageContaining("Secure Fields init");
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403})
    void throws_datatransAuthenticationException_whenMobileSdkInitCredentialsAreRejected(
        int status) {
      mockWebServer.enqueue(credentialsResponse(status));

      var request = new DatatransMobileSdkRequest(
          8600L, "GBP", REFNO, List.of("VIS"), MERCHANT_ID, null);

      assertThatThrownBy(() -> adapter.initMobileSdk(request))
          .isInstanceOf(DatatransAuthenticationException.class)
          .hasMessageContaining("Mobile SDK init");
    }

    @Test
    void logs_atError_withStatusAndBody_butNeverTheMerchantPassword() {
      mockWebServer.enqueue(credentialsResponse(401));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(DatatransAuthenticationException.class);

      assertThat(logAppender.list)
          .anySatisfy(event -> {
            assertThat(event.getLevel()).isEqualTo(Level.ERROR);
            assertThat(event.getFormattedMessage())
                .contains("Datatrans rejected merchant credentials")
                .contains("401")
                .contains(MERCHANT_ID)
                .contains("UNAUTHORIZED_MERCHANT")
                .doesNotContain(MERCHANT_PASSWORD);
          });
    }

    /**
     * The status endpoint answering {@code 401} while resolving an authorize conflict must not
     * be laundered into a 3-D Secure failure — the credentials fault is what happened.
     */
    @Test
    void throws_datatransAuthenticationException_when409RecoveryStatusCredentialsAreRejected() {
      mockWebServer.enqueue(conflictResponse(409, "TRANSACTION_ALREADY_AUTHORIZED"));
      mockWebServer.enqueue(credentialsResponse(401));

      assertThatThrownBy(() -> adapter.authorizeTransaction(TRANSACTION_ID, REFNO, 8600L, MERCHANT_ID))
          .isInstanceOf(DatatransAuthenticationException.class)
          .isNotInstanceOf(ThreeDsAuthenticationFailedException.class);
    }
  }

  // ==========================================================================
  // Helpers
  // ==========================================================================

  private MockResponse credentialsResponse(int statusCode) {
    return new MockResponse()
        .setResponseCode(statusCode)
        .setHeader("Content-Type", "application/json")
        .setBody("""
            {
              "error": {
                "code": "UNAUTHORIZED_MERCHANT",
                "message": "unauthorized"
              }
            }
            """);
  }

  private MockResponse conflictResponse(int statusCode, String errorCode) {
    return new MockResponse()
        .setResponseCode(statusCode)
        .setHeader("Content-Type", "application/json")
        .setBody("""
            {
              "error": {
                "code": "%s",
                "message": "the transaction is not in a valid state for this operation"
              }
            }
            """.formatted(errorCode));
  }

  private MockResponse statusResponse(String status, Integer authorizedAmount, String refno) {
    return new MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody("""
            {
              "transactionId": "%s",
              "status": "%s",
              "currency": "GBP",
              %s
              "refno": "%s",
              "acquirerAuthorizationCode": "160600",
              "paymentMethod": "VIS",
              "card": {
                "alias": "AAABcH0Bq92s3kgAESIAAbGj5NIs",
                "masked": "424242xxxxxx4242",
                "expiryMonth": "12",
                "expiryYear": "28"
              }
            }
            """.formatted(TRANSACTION_ID, status,
            authorizedAmount == null ? "" : "\"authorizedAmount\": " + authorizedAmount + ",",
            refno));
  }
}
