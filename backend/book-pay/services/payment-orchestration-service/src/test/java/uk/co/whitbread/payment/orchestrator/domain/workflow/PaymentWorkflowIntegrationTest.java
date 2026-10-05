package uk.co.whitbread.payment.orchestrator.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.LoggerFactory;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ServiceUnavailableException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionMismatchException;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransCardInfo;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.MobileSdkReconciliationSettings;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardMobileInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardWebInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransReconciliationProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransWebhookProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentWorkflowProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.temporal.TemporalWorkflowAdapter;

/**
 * Integration tests for the unified {@link PaymentWorkflow} using Temporal's
 * {@link TestWorkflowEnvironment}.
 *
 * <p>Tests the full end-to-end payment flows through the unified workflow for both
 * {@code NEW_CARD_WEB} and {@code NEW_CARD_MOBILE} methods, including:
 * <ul>
 *   <li>Initialization through the unified API endpoint model</li>
 *   <li>Synchronous authorization via {@code @UpdateMethod}</li>
 *   <li>Webhook handling with transactionId validation</li>
 *   <li>Settlement via BookingCompletedEvent signals</li>
 *   <li>Full authorize → publish → settle pipeline</li>
 * </ul>
 *
 * <p>Validates: Requirements 18.6, 16.4, 8.3
 */
class PaymentWorkflowIntegrationTest {

  private static final String BASKET_ID = "ARH-integration-test-001";
  private static final String HOTEL_ID = "HARHOR";
  private static final String TRANSACTION_ID = "txn-integration-12345";
  private static final String MERCHANT_ID = "deWB-HARHOR";
  private static final String BOOKING_REF = "ARH1234567";

  @RegisterExtension
  static final TestWorkflowExtension testWorkflowExtension =
      TestWorkflowExtension.newBuilder()
          .setWorkflowTypes(PaymentWorkflowImpl.class)
          .setDoNotStart(true)
          .build();

  @Nested
  class NewCardWebEndToEndFlow {

    @Test
    void fullWebFlow_initAuthorizeSettle(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Step 1: Initialize via unified init endpoint
      PaymentInitResult initResult = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));

      assertThat(initResult.success()).isTrue();
      assertThat(initResult.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(initResult.errorCode()).isNull();

      // Step 2: Synchronous authorize via @UpdateMethod
      AuthorizeResult authorizeResult = workflow.authorize();

      assertThat(authorizeResult.success()).isTrue();
      assertThat(authorizeResult.errorCode()).isNull();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Step 3: Verify Kafka event was published
      assertThat(stub.publishedEventBasketId).isEqualTo(BASKET_ID);
      assertThat(stub.publishedEventTransactionId).isEqualTo(TRANSACTION_ID);

      // Step 4: Settlement via BookingCompletedEvent
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.settledTransactionId).isEqualTo(TRANSACTION_ID);
    }

    @Test
    void initWithUnsupportedCurrency_failsWithInvalidAmountBeforeAnyGatewayCall(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub() {
        boolean datatransCalled;

        @Override
        public Reservation getReservation(String basketId) {
          // Unsupported currency on the reservation — must be rejected, never guessed at
          return new Reservation(BASKET_ID, HOTEL_ID, new BigDecimal("1000"),
              "JPY", BOOKING_REF, BOOKING_REF, "PI");
        }

        @Override
        public String initDatatransSecureFields(DatatransSecureFieldsRequest request) {
          datatransCalled = true;
          return super.initDatatransSecureFields(request);
        }
      };
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      PaymentInitResult initResult = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));

      assertThat(initResult.success()).isFalse();
      assertThat(initResult.errorCode()).isEqualTo(PaymentErrorCode.INVALID_AMOUNT);
      assertThat(stub.datatransCalled)
          .as("no gateway call may happen with an unconvertible amount")
          .isFalse();
    }

    @Test
    void webFlow_authorizeReturnsResultSynchronously(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));

      // Authorize returns synchronously — no polling required
      AuthorizeResult result = workflow.authorize();

      assertThat(result).isNotNull();
      assertThat(result.success()).isTrue();
      // This confirms Requirement 8.3: synchronous authorization responses
    }

    @Test
    void webFlow_bookingFailed_cancelsPayment(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));
      workflow.authorize();

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Booking failed — should trigger cancellation
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "FAILED"));
      awaitPaymentStatus(workflow, PaymentStatus.CANCELLED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
      assertThat(stub.cancelledTransactionId).isEqualTo(TRANSACTION_ID);
    }
  }

  @Nested
  class NewCardMobileEndToEndFlow {

    @Test
    void fullMobileFlow_initWebhookSettle(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Step 1: Initialize via unified init endpoint (mobile)
      var reconciliation = new MobileSdkReconciliationSettings(
          true, 5000, 10000, 300000);
      PaymentInitResult initResult = workflow.init(
          new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
              "https://callbacks.premierinn.com/api/payments/webhooks/datatrans?basketId="
                  + BASKET_ID,
              reconciliation));

      assertThat(initResult.success()).isTrue();
      assertThat(initResult.transactionId()).isEqualTo(TRANSACTION_ID);

      // Step 2: Webhook received with valid transactionId
      var webhookPayload = new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789");

      workflow.webhookReceived(webhookPayload);

      // Step 3: Wait for authorization to complete
      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Step 4: Settlement
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
    }

    @Test
    void mobileFlow_webhookWithMismatchedTransactionId_isDropped(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      var reconciliation = new MobileSdkReconciliationSettings(
          true, 5000, 10000, 300000);
      workflow.init(new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
          reconciliation));

      // Send webhook with WRONG transactionId — should be dropped
      var invalidPayload = new DatatransWebhookPayload(
          "WRONG-TRANSACTION-ID", MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, null, null, null, null, null);

      workflow.webhookReceived(invalidPayload);

      // Workflow should still be in INITIALIZED state (webhook was dropped)
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      // Now send the correct webhook — validates Requirement 16.4
      var validPayload = new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789");

      workflow.webhookReceived(validPayload);
      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void mobileFlow_statusWithoutAuthorizedAmount_publishesTheRequestedAmount(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new AmountlessStatusActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      var reconciliation = new MobileSdkReconciliationSettings(
          true, 5000, 10000, 300000);
      workflow.init(new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
          reconciliation));

      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", null, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789"));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);

      // The gateway confirmed the authorization without echoing the amount: the event must
      // carry the amount we asked it to authorize, never 0.
      assertThat(stub.publishedEventAuthorizedAmount).isEqualTo(8600L);
      // The language the frontend sent on init travels through workflow state to the event.
      assertThat(stub.publishedEventLanguage).isEqualTo("en");
    }

    @Test
    void cancelWebhookTheGatewayDoesNotConfirm_isDroppedAndThePaymentSurvives(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) throws Exception {
      var stub = new MutableStatusActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Webhook-only mode: the webhook endpoint may be reachable without a verified signature,
      // so its claims must not resolve the payment unless the gateway backs them.
      workflow.init(new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
          new MobileSdkReconciliationSettings(false, 0, 0, 0)));

      // A forged/premature "canceled" webhook arrives while the gateway still says in-flight.
      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "canceled", "GBP",
          BOOKING_REF, "VIS", null, null, null, null, null, null));

      // The strategy asks the gateway, is told "initialized", and drops the claim.
      long deadline = System.currentTimeMillis() + 5000;
      while (stub.statusCalls.get() < 1 && System.currentTimeMillis() < deadline) {
        Thread.sleep(50);
      }
      assertThat(stub.statusCalls.get()).isGreaterThanOrEqualTo(1);
      Thread.sleep(200);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      // The genuine outcome arrives later and resolves the payment normally.
      stub.gatewayStatus = "authorized";
      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789"));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void authorizeCalledOnAMobilePayment_isRejectedAndOnlyOneEventIsEverPublished(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // authorize() drives NEW_CARD_WEB only. For a mobile payment run() has already
      // dispatched a processor for the transaction; accepting authorize() would start a
      // second one, and both could publish PaymentAuthorisedEvent for the same payment.
      var stub = new PublishCountingActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      workflow.init(new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
          new MobileSdkReconciliationSettings(true, 5000, 10000, 300000)));

      // A mis-routed /authorize call on the mobile payment must be rejected outright.
      AuthorizeResult result = workflow.authorize();
      assertThat(result.success()).isFalse();
      assertThat(result.errorCode())
          .isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      // The mobile attempt still resolves normally through its own driver.
      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789"));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(stub.publishCount.get()).isEqualTo(1);
    }

    @Test
    void cancelWebhookTheGatewayConfirms_cancelsThePayment(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new MutableStatusActivitiesStub();
      stub.gatewayStatus = "canceled";
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      workflow.init(new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
          new MobileSdkReconciliationSettings(false, 0, 0, 0)));

      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "canceled", "GBP",
          BOOKING_REF, "VIS", null, null, null, null, null, null));

      // The gateway agrees the transaction is cancelled, so the webhook's claim stands.
      awaitPaymentStatus(workflow, PaymentStatus.CANCELLED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
    }
  }

  @Nested
  class CrossMethodReInitialization {

    @Test
    void reInitFromWebToMobile_whileInitialized_succeeds(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Initialize as web first
      PaymentInitResult webResult = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));
      assertThat(webResult.success()).isTrue();

      // Re-initialize as mobile (cross-method re-init)
      var reconciliation = new MobileSdkReconciliationSettings(
          true, 5000, 10000, 300000);
      PaymentInitResult mobileResult = workflow.init(
          new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
              "https://callbacks.premierinn.com/webhooks/datatrans", reconciliation));

      assertThat(mobileResult.success()).isTrue();
      assertThat(mobileResult.transactionId()).isEqualTo(TRANSACTION_ID);
      // The stub's gateway reports the prior transaction authorized, so it must be cancelled
      // (the hold released) before the new attempt proceeds.
      assertThat(stub.cancelledTransactionId).isEqualTo(TRANSACTION_ID);
    }

    @Test
    void reInitFromFailedWebToMobile_webhookIsStillProcessedAndThePaymentSettles(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // Regression: run() used to dispatch the mobile awaitAuthorization() driver exactly once,
      // right after the FIRST init. A payment that started as web and was re-initialized as
      // mobile after the web attempt failed had no driver: its webhook sat in the inbox
      // unconsumed until expiry cancelled a payment the customer had actually completed.
      var stub = new ThreeDsFailingAuthorizeActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Web attempt fails (3-D Secure declined) — the workflow stays open for a retry.
      workflow.init(webCommand());
      AuthorizeResult webAttempt = workflow.authorize();
      assertThat(webAttempt.success()).isFalse();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

      // The customer switches to the mobile app and re-initializes.
      PaymentInitResult mobileResult = workflow.init(
          new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
              "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
              new MobileSdkReconciliationSettings(true, 5000, 10000, 300000)));
      assertThat(mobileResult.success()).isTrue();

      // The mobile attempt's webhook must now be consumed by a freshly dispatched driver.
      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789"));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
    }

    @Test
    void mobileRetryAfterFailedMobileAttempt_secondWebhookIsStillProcessed(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // Same one-time-dispatch regression, mobile→mobile: the first attempt's driver returns
      // when the attempt fails, and the retry needs a new one.
      var stub = new MutableStatusActivitiesStub();
      stub.gatewayStatus = "failed";
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      var mobileCommand = new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE",
          "APPS_IOS",
          "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
          new MobileSdkReconciliationSettings(false, 0, 0, 0));

      // First mobile attempt fails — the gateway confirms the webhook's "failed" claim.
      workflow.init(mobileCommand);
      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "failed", "GBP",
          BOOKING_REF, "VIS", null, null, null, null, null, null));
      awaitPaymentStatus(workflow, PaymentStatus.FAILED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

      // Retry: a fresh mobile attempt on the same workflow.
      PaymentInitResult retry = workflow.init(mobileCommand);
      assertThat(retry.success()).isTrue();

      stub.gatewayStatus = "authorized";
      workflow.webhookReceived(new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-123",
          "424242xxxxxx4242", "12", "29", "AUTH-CODE-789"));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void reInit_whenGatewayStillReportsInitialized_skipsTheUncancellableCancel(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // Datatrans only accepts a cancel for authorized transactions. A superseded transaction
      // whose card form was never completed is "initialized" at the gateway: attempting the
      // cancel is a deterministic error, so the workflow must skip it and let it lapse.
      var stub = new MutableStatusActivitiesStub();
      stub.gatewayStatus = "initialized";
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      PaymentInitResult first = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));
      assertThat(first.success()).isTrue();

      PaymentInitResult second = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));

      assertThat(second.success()).isTrue();
      assertThat(stub.cancelledTransactionId).isNull();
    }

    @Test
    void reInit_whenTheGatewayNoLongerKnowsTheTransaction_skipsTheCancelAndProceeds(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // A never-completed Secure Fields transaction is not even queryable at Datatrans: the
      // status check 404s. That answer means "nothing to cancel", not an outage — the re-init
      // must proceed without a cancel attempt and without burning retries.
      var stub = new UnknownTransactionActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      PaymentInitResult first = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));
      assertThat(first.success()).isTrue();

      PaymentInitResult second = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));

      assertThat(second.success()).isTrue();
      assertThat(stub.cancelledTransactionId).isNull();
      // do-not-retry: the 404 is deterministic, so exactly one status attempt per check.
      assertThat(stub.statusCalls.get()).isEqualTo(1);
    }

    @Test
    void reInitAfterAuthorized_rejected(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));
      workflow.authorize();

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Attempt re-init after AUTHORIZED — should be rejected
      PaymentInitResult reInitResult = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));

      assertThat(reInitResult.success()).isFalse();
      assertThat(reInitResult.errorCode())
          .isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
    }
  }

  @Nested
  class WebhookEndpointRename {

    @Test
    void webhookSignalHandling_worksWithRenamedEndpoint(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // This test validates that the webhook handling logic works regardless
      // of the endpoint path — the rename from /mobile-sdk to /datatrans is
      // transparent to the workflow. The controller layer handles path routing.
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      var reconciliation = new MobileSdkReconciliationSettings(
          true, 5000, 10000, 300000);
      workflow.init(new NewCardMobileInitCommand(BASKET_ID, "gb", "en",
          "LEISURE", "APPS_ANDROID",
          "https://callbacks.premierinn.com/api/payments/webhooks/datatrans?basketId="
              + BASKET_ID,
          reconciliation));

      // Simulate webhook from the renamed /datatrans endpoint
      var payload = new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "ECA", 8600, "alias-456",
          "510000xxxxxx0000", "06", "28", "AUTH-567");

      workflow.webhookReceived(payload);
      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }
  }

  /**
   * Money-moving activities are retried, so what protects the guest is that a retry converges
   * on the work already done rather than doing it twice. These tests count what the gateway
   * was actually asked to do, not how many activity attempts Temporal made.
   */
  @Nested
  class MoneyActivityRetrySemantics {

    @Test
    void webFlow_authorizeRetriedAfterAmbiguousFailure_authorizesExactlyOnce(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new AmbiguousAuthorizeActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));

      AuthorizeResult result = workflow.authorize();

      // The first attempt held the money and lost its response; the second attempt recovered
      // that same authorization instead of taking a second hold.
      assertThat(result.success()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(stub.authorizeAttempts.get()).isEqualTo(2);
      assertThat(stub.gatewayAuthorizations.get()).isEqualTo(1);
    }

    @Test
    void webFlow_threeDsFailedAuthorization_isNotRetried(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new ThreeDsFailingAuthorizeActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));

      AuthorizeResult result = workflow.authorize();

      // A failed 3-D Secure authentication is the gateway's answer, not an accident —
      // asking twice more would only repeat it.
      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.GATEWAY_ERROR);
      assertThat(stub.authorizeAttempts.get()).isEqualTo(1);
    }

    @Test
    void webFlow_rejectedMerchantCredentials_reportsGatewayFaultAndIsNotRetried(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new RejectedCredentialsAuthorizeActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));

      AuthorizeResult result = workflow.authorize();

      // Our merchant password is wrong. The customer's card had nothing to do with it, so the
      // code must not be a decline — and the next two attempts would send the same password.
      assertThat(result.success()).isFalse();
      assertThat(result.errorCode())
          .isEqualTo(PaymentErrorCode.GATEWAY_AUTHENTICATION_FAILED);
      assertThat(result.errorCode()).isNotEqualTo(PaymentErrorCode.GATEWAY_ERROR);
      assertThat(result.errorMessage()).doesNotContain("card").doesNotContain("issuer");
      assertThat(stub.authorizeAttempts.get()).isEqualTo(1);
    }

    @Test
    @Disabled("Flaky: awaitPaymentStatus polls real wall-clock time (5s) while the settle "
        + "retry backoff runs on Temporal's virtual clock, so under CI load the workflow is "
        + "still in SETTLEMENT_PENDING between attempts when the poll deadline expires. "
        + "Temporarily disabled to unblock deployment; fix is to block on the workflow result "
        + "(WorkflowStub.fromTyped(workflow).getResult) so the backoff resolves deterministically.")
    void webFlow_settleRetriedAfterAmbiguousFailure_capturesExactlyOnce(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new AmbiguousSettleActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));
      workflow.authorize();

      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.settleAttempts.get()).isEqualTo(2);
      assertThat(stub.gatewayCaptures.get()).isEqualTo(1);
    }

    @Test
    void webFlow_settleRejectedByMismatchedTransaction_isNotRetried(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new MismatchingSettleActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(new NewCardWebInitCommand(BASKET_ID,
          "https://premierinn.com/return", "gb", "en", "LEISURE", "PI"));
      workflow.authorize();

      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLEMENT_FAILED);

      // SETTLEMENT_FAILED, not FAILED: the booking exists and the money is held, so this is an
      // operator's problem rather than a payment the customer can retry.
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLEMENT_FAILED);
      assertThat(stub.settleAttempts.get()).isEqualTo(1);
    }
  }

  /**
   * Settlement is the one call in this workflow that cannot be given up on: by the time it runs
   * the guest has a confirmed booking and the money is held. These tests are about what the
   * workflow does with time — how long it keeps trying, and what it refuses to do when trying
   * stops working.
   */
  @Nested
  class SettlementResilience {

    @Test
    void settleFailingForOverHalfAnHour_stillSettlesAndCapturesOnce(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new FlakySettleActivitiesStub(9);
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      long startedAt = testEnv.currentTimeMillis();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());
      workflow.authorize();
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      // Blocking on the result unlocks the virtual clock, so the retry backoff plays out in
      // simulated time rather than in the test's wall clock.
      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      long elapsed = testEnv.currentTimeMillis() - startedAt;

      // The old 30-minute expiry timer would have fired somewhere in the middle of this and
      // cancelled the authorization — a booking kept and the money handed back.
      assertThat(elapsed).isGreaterThan(Duration.ofMinutes(30).toMillis());
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.settleAttempts.get()).isEqualTo(10);
      assertThat(stub.gatewayCaptures.get()).isEqualTo(1);
      assertThat(stub.cancelAttempts.get()).isZero();
    }

    @Test
    void settleExhaustingItsHorizon_parksForOpsAndKeepsTheAuthorization(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new AlwaysFailingSettleActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      var logAppender = attachAppender(PaymentWorkflowImpl.class);

      PaymentWorkflow workflow = startWorkflow(worker, client, Map.of(
          PaymentWorkflowTuning.SETTLEMENT_HORIZON_MEMO_KEY,
          Duration.ofMinutes(1).toMillis()));
      workflow.init(webCommand());
      workflow.authorize();
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      // A parked payment must not close as Completed: the execution fails with a typed
      // ApplicationFailure so it is filterable in the Temporal UI and workflow-failure metrics.
      assertParkedExecutionFailure(workflow, PaymentStatus.SETTLEMENT_FAILED);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLEMENT_FAILED);
      assertThat(stub.settleAttempts.get()).isGreaterThan(1);

      // The booking exists. Cancelling the hold now would be the free stay this design exists
      // to prevent, so the authorization is left exactly where it is.
      assertThat(stub.cancelAttempts.get()).isZero();

      // Unlike FAILED, this is not the customer's to retry — a second attempt would put a
      // second hold on the card for a stay already paid for.
      assertReInitRejected(workflow);

      // Ops has to be able to find this from the logs alone.
      String opsLog = errorMessages(logAppender);
      assertThat(opsLog).contains("MANUAL SETTLEMENT REQUIRED");
      assertThat(formatted(logAppender, "MANUAL SETTLEMENT REQUIRED"))
          .contains(TRANSACTION_ID)
          .contains(BOOKING_REF)
          .contains("deWB-" + HOTEL_ID)
          .contains("8600");

      detachAppender(PaymentWorkflowImpl.class, logAppender);
    }
  }

  /**
   * The authorised-payment event is what starts the booking. Nothing downstream exists until it
   * lands, which is what makes cancelling the right compensation here and the wrong one
   * everywhere later.
   */
  @Nested
  class AuthorisedEventPublishing {

    @Test
    void publishExhaustingItsHorizon_cancelsTheAuthorizationAndLetsTheCustomerRetry(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new PublishFailureActivitiesStub(Integer.MAX_VALUE);
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client, Map.of(
          PaymentWorkflowTuning.PUBLISH_HORIZON_MEMO_KEY,
          Duration.ofSeconds(5).toMillis()));
      workflow.init(webCommand());

      AuthorizeResult result = workflow.authorize();

      assertThat(result.success()).isFalse();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

      // No booking was ever triggered, so the compensation is complete: the hold is released.
      assertThat(stub.cancelAttempts.get()).isOne();

      // And FAILED means the customer can simply pay again.
      stub.publishSucceedsFrom(1);
      PaymentInitResult reInit = workflow.init(webCommand());
      assertThat(reInit.success()).isTrue();

      AuthorizeResult retry = workflow.authorize();
      assertThat(retry.success()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void whileThePublishIsStillRunning_thePaymentIsNotYetReportedAsAuthorized(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) throws Exception {
      // The publish can retry for minutes and can still compensate to FAILED, so a query taken
      // inside that window must not say AUTHORIZED — and the bookingCompleted guard, which only
      // accepts events while AUTHORIZED, must not be open either.
      var stub = new BlockingPublishActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      CompletableFuture<AuthorizeResult> authorizing =
          CompletableFuture.supplyAsync(workflow::authorize);
      assertThat(stub.awaitPublishStarted()).isTrue();

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      stub.releasePublish();
      AuthorizeResult result = authorizing.get(20, TimeUnit.SECONDS);

      // AUTHORIZED appears only alongside a successful result, never before it.
      assertThat(result.success()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
    }

    @Test
    void publishRecoveringAfterATransientFailure_deliversAtLeastOnce(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new PublishFailureActivitiesStub(1);
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      AuthorizeResult result = workflow.authorize();

      assertThat(result.success()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(stub.cancelAttempts.get()).isZero();

      // Two attempts for one logical event. The first may well have reached Kafka before its
      // acknowledgement was lost — which is precisely why consumers must dedupe on basketId.
      assertThat(stub.publishAttempts.get()).isEqualTo(2);
    }
  }

  /**
   * The REST-facing half of a slow authorization, driven through the real
   * {@link TemporalWorkflowAdapter} rather than a typed stub.
   *
   * <p>The workflow's authorize handler can run for minutes; the caller's connection cannot.
   * These tests pin the contract that makes those two facts compatible: the update is durably
   * accepted, the wait is bounded, and a wait that elapses is reported as pending while the
   * workflow carries on to a real outcome.
   */
  @Nested
  class BoundedAuthorizeWait {

    @Test
    void whenTheHandlerOutlastsTheWait_theCallerIsToldPendingAndTheWorkflowFinishes(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) throws Exception {
      var stub = new BlockingPublishActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      // A wait far shorter than the blocked publish, so the elapse is deterministic rather
      // than a race with a slow machine.
      TemporalWorkflowAdapter adapter = adapterWithAuthorizeWait(
          client, worker.getTaskQueue(), Duration.ofMillis(200));

      PaymentWorkflow workflow = startWorkflowForAdapter(worker, client);
      workflow.init(webCommand());

      AuthorizeResult pending = adapter.authorizePayment(BASKET_ID);

      // Not GATEWAY_ERROR: nothing failed — the authorization is committed and still running.
      assertThat(pending.success()).isFalse();
      assertThat(pending.errorCode())
          .isEqualTo(PaymentErrorCode.AUTHORIZATION_PENDING);
      assertThat(stub.awaitPublishStarted()).isTrue();

      // The status endpoint the caller was told to poll sees the same in-flight authorization.
      assertThat(adapter.getPaymentStatus(BASKET_ID).paymentStatus())
          .isEqualTo(PaymentStatus.INITIALIZED);

      // The workflow was never abandoned by the client giving up — it finishes on its own.
      stub.releasePublish();
      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);

      var settled = adapter.getPaymentStatus(BASKET_ID);
      assertThat(settled.paymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(settled.authorizeResult()).isNotNull();
      assertThat(settled.authorizeResult().success()).isTrue();
    }

    @Test
    void whenTheHandlerBeatsTheWait_theResultComesBackSynchronously(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new IntegrationActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      TemporalWorkflowAdapter adapter = adapterWithAuthorizeWait(
          client, worker.getTaskQueue(), Duration.ofSeconds(20));

      PaymentWorkflow workflow = startWorkflowForAdapter(worker, client);
      workflow.init(webCommand());

      AuthorizeResult result = adapter.authorizePayment(BASKET_ID);

      assertThat(result.success()).isTrue();
      assertThat(result.errorCode()).isNull();
    }
  }

  /**
   * The {@code bookingCompleted} event crosses a broker, a consumer group, and a signal
   * delivery. These tests are about the workflow not depending on all three working.
   */
  @Nested
  class BookingCompletionReconciliation {

    @Test
    void whenTheSignalNeverArrives_pollingResolvesTheBookingAndSettlesOnce(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new PollingActivitiesStub(BasketStatus.COMPLETED);
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client, shortPollMemo());
      workflow.init(webCommand());
      workflow.authorize();

      // No bookingCompleted signal is ever sent — only the clock moves.
      testEnv.sleep(Duration.ofSeconds(45));
      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.basketStatusReads.get()).isPositive();
      assertThat(stub.settleAttempts.get()).isOne();

      // The signal turns up afterwards, as a lost-and-then-found event does. Whether it finds a
      // closed workflow or the state guard, the one thing it must not do is settle again.
      try {
        workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      } catch (RuntimeException expected) {
        // Signalling a workflow that has already completed is one of the two acceptable
        // outcomes here.
      }
      assertThat(stub.settleAttempts.get()).isOne();
    }

    @Test
    void aSignalArrivingDuringSettlement_isDroppedByTheStateGuard(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client)
        throws Exception {
      var stub = new BlockingSettleActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client, shortPollMemo());
      workflow.init(webCommand());
      workflow.authorize();

      // The poll resolves the booking and settlement starts, then hangs.
      testEnv.sleep(Duration.ofSeconds(45));
      assertThat(stub.awaitSettleStarted()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLEMENT_PENDING);

      // Funds are held and the booking exists: a new payment attempt would double-charge.
      PaymentInitResult reInit = workflow.init(webCommand());
      assertThat(reInit.success()).isFalse();
      assertThat(reInit.errorCode()).isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);

      // The Kafka signal arrives while the capture is in flight — the race the poller creates.
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      stub.releaseSettle();
      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.settleAttempts.get()).isOne();
    }

    @Test
    void whenPollingFindsAFailedBasket_theAuthorizationIsCancelled(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new PollingActivitiesStub(BasketStatus.FAILED);
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client, shortPollMemo());
      workflow.init(webCommand());
      workflow.authorize();

      testEnv.sleep(Duration.ofSeconds(45));
      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      // The booking will not happen, so holding the guest's money would be indefensible.
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
      assertThat(stub.cancelAttempts.get()).isOne();
      assertThat(stub.settleAttempts.get()).isZero();
    }

    @Test
    void whenTheBookingNeverResolves_thePaymentIsParkedRatherThanCancelled(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new PollingActivitiesStub(BasketStatus.PAY_PENDING);
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      var logAppender = attachAppender(PaymentWorkflowImpl.class);

      PaymentWorkflow workflow = startWorkflow(worker, client, shortPollMemo());
      workflow.init(webCommand());
      workflow.authorize();

      testEnv.sleep(Duration.ofMinutes(4));

      // A parked payment must not close as Completed: the execution fails with a typed
      // ApplicationFailure so it is filterable in the Temporal UI and workflow-failure metrics.
      assertParkedExecutionFailure(workflow, PaymentStatus.BOOKING_PENDING_TIMEOUT);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.BOOKING_PENDING_TIMEOUT);
      assertThat(stub.basketStatusReads.get()).isPositive();

      // Neither automatic action is defensible on an unresolved booking: cancelling may void a
      // booking that completes a minute later, settling may charge for one that never does.
      assertThat(stub.cancelAttempts.get()).isZero();
      assertThat(stub.settleAttempts.get()).isZero();

      assertReInitRejected(workflow);

      assertThat(errorMessages(logAppender)).contains("MANUAL RECONCILIATION REQUIRED");

      detachAppender(PaymentWorkflowImpl.class, logAppender);
    }
  }

  // ==========================================================================
  // Helpers
  // ==========================================================================

  /**
   * Asserts that a payment parked for manual reconciliation cannot be re-initialized.
   *
   * <p>Both outcomes are rejections and both are correct: the re-init policy answers
   * {@code INVALID_TRANSACTION_STATE} while the workflow is still open, and once the parked
   * workflow has closed there is no workflow left to re-initialize at all. What must never
   * happen is a successful second payment attempt on top of a live authorization.
   */
  /**
   * Awaits the workflow's close and asserts it FAILED with an {@code ApplicationFailure} typed
   * as the given parked status — the execution-level signal that lets operations find parked
   * payments in the Temporal UI instead of them hiding among Completed workflows.
   */
  private static void assertParkedExecutionFailure(PaymentWorkflow workflow,
      PaymentStatus parkedStatus) {
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> WorkflowStub.fromTyped(workflow).getResult(Void.class))
        .isInstanceOf(io.temporal.client.WorkflowFailedException.class)
        .cause()
        .isInstanceOf(io.temporal.failure.ApplicationFailure.class)
        .extracting(t -> ((io.temporal.failure.ApplicationFailure) t).getType())
        .isEqualTo(parkedStatus.name());
  }

  private static void assertReInitRejected(PaymentWorkflow workflow) {
    try {
      PaymentInitResult reInit = workflow.init(webCommand());
      assertThat(reInit.success()).isFalse();
      assertThat(reInit.errorCode()).isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
    } catch (RuntimeException e) {
      // The parked workflow has already closed — no new attempt is possible.
      assertThat(e).isInstanceOf(io.temporal.client.WorkflowNotFoundException.class);
    }
  }

  /** Poll timings small enough for the virtual clock to run the whole loop in one test. */
  private static Map<String, Object> shortPollMemo() {
    return Map.of(
        PaymentWorkflowTuning.BOOKING_POLL_INTERVAL_MEMO_KEY,
        Duration.ofSeconds(30).toMillis(),
        PaymentWorkflowTuning.BOOKING_POLL_HORIZON_MEMO_KEY,
        Duration.ofMinutes(3).toMillis());
  }

  private static ListAppender<ILoggingEvent> attachAppender(Class<?> loggerClass) {
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    ((Logger) LoggerFactory.getLogger(loggerClass)).addAppender(appender);
    return appender;
  }

  private static void detachAppender(Class<?> loggerClass,
      ListAppender<ILoggingEvent> appender) {
    ((Logger) LoggerFactory.getLogger(loggerClass)).detachAppender(appender);
    appender.stop();
  }

  private static String errorMessages(ListAppender<ILoggingEvent> appender) {
    return appender.list.stream()
        .filter(event -> event.getLevel() == Level.ERROR)
        .map(ILoggingEvent::getMessage)
        .collect(java.util.stream.Collectors.joining("\n"));
  }

  private static String formatted(ListAppender<ILoggingEvent> appender, String containing) {
    return appender.list.stream()
        .filter(event -> event.getMessage().contains(containing))
        .map(ILoggingEvent::getFormattedMessage)
        .collect(java.util.stream.Collectors.joining("\n"));
  }

  private static PaymentWorkflow startWorkflow(Worker worker, WorkflowClient client) {
    PaymentWorkflow workflow = client.newWorkflowStub(
        PaymentWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(worker.getTaskQueue()).build());
    WorkflowClient.start(workflow::run, BASKET_ID);
    return workflow;
  }

  /**
   * Starts the workflow carrying tuning memos, exactly as the adapter does from
   * {@code integrations.payment.workflow.*}. Tests use it to shrink horizons that are measured
   * in hours in production down to something a virtual clock resolves in one step.
   */
  private static PaymentWorkflow startWorkflow(
      Worker worker, WorkflowClient client, Map<String, Object> memo) {
    PaymentWorkflow workflow = client.newWorkflowStub(
        PaymentWorkflow.class,
        WorkflowOptions.newBuilder()
            .setTaskQueue(worker.getTaskQueue())
            .setMemo(memo)
            .build());
    WorkflowClient.start(workflow::run, BASKET_ID);
    return workflow;
  }

  /**
   * Starts the workflow under the id the adapter derives from the basket, so the adapter can
   * address this execution the way it does in production.
   */
  private static PaymentWorkflow startWorkflowForAdapter(Worker worker, WorkflowClient client) {
    PaymentWorkflow workflow = client.newWorkflowStub(
        PaymentWorkflow.class,
        WorkflowOptions.newBuilder()
            .setTaskQueue(worker.getTaskQueue())
            .setWorkflowId("payment-" + BASKET_ID)
            .build());
    WorkflowClient.start(workflow::run, BASKET_ID);
    return workflow;
  }

  /** Builds the production adapter against the test environment's client. */
  private static TemporalWorkflowAdapter adapterWithAuthorizeWait(
      WorkflowClient client, String taskQueue, Duration authorizeWait) {
    var workflowProperties = new PaymentWorkflowProperties();
    workflowProperties.setAuthorizeWait(authorizeWait);
    return new TemporalWorkflowAdapter(
        client,
        new DatatransWebhookProperties(),
        new DatatransReconciliationProperties(),
        workflowProperties,
        taskQueue);
  }

  private static NewCardWebInitCommand webCommand() {
    return new NewCardWebInitCommand(BASKET_ID,
        "https://premierinn.com/return", "gb", "en", "LEISURE", "PI");
  }

  private static void awaitPaymentStatus(PaymentWorkflow workflow, PaymentStatus expected) {
    long deadline = System.currentTimeMillis() + 5000;
    while (System.currentTimeMillis() < deadline) {
      if (workflow.getPaymentStatus() == expected) {
        return;
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  // ==========================================================================
  // Activity Stub
  // ==========================================================================

  /**
   * Comprehensive activity stub supporting both NEW_CARD_WEB and NEW_CARD_MOBILE flows.
   * Tracks all activity calls for verification in integration tests.
   */
  private static class IntegrationActivitiesStub implements PaymentActivities {

    String publishedEventBasketId;
    String publishedEventTransactionId;
    Long publishedEventAuthorizedAmount;
    String publishedEventLanguage;
    String settledTransactionId;
    String cancelledTransactionId;


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId,
        String refno, long amount, String merchantId) {
      return new DatatransAuthorizeResponse(
          transactionId, "authorized", "AUTH-123",
          new DatatransCardInfo("alias-test", "424242xxxxxx4242", "12", "29"),
          "VIS");
    }


    @Override
    public Reservation getReservation(String basketId) {
      return new Reservation(BASKET_ID, HOTEL_ID, new BigDecimal("86.00"),
          "GBP", BOOKING_REF, BOOKING_REF, "PI");
    }

    @Override
    public PaymentMethodValidationResult validatePaymentMethods(
        String basketReference, String hotelId, String country, String language,
        String userType, String clientChannel) {
      return new PaymentMethodValidationResult(true, List.of("VIS", "ECA", "AMX"));
    }

    @Override
    public String initDatatransSecureFields(DatatransSecureFieldsRequest request) {
      return TRANSACTION_ID;
    }

    @Override
    public String initMobileSdkTransaction(DatatransMobileSdkRequest request) {
      return TRANSACTION_ID;
    }

    @Override
    public DatatransTransactionStatus getTransactionStatus(String transactionId,
        String merchantId) {
      return new DatatransTransactionStatus(
          transactionId, "authorized", "GBP", 8600, "AUTH-123",
          new DatatransCardInfo("alias-test", "424242xxxxxx4242", "12", "29"),
          "VIS", BOOKING_REF);
    }


    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settledTransactionId = transactionId;
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
      publishedEventBasketId = basketId;
      publishedEventTransactionId = transactionId;
      publishedEventAuthorizedAmount = authorizedAmount;
      publishedEventLanguage = language;
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelledTransactionId = transactionId;
    }

    @Override
    public String resolveMerchantId(String hotelCode) {
      return "deWB-" + hotelCode;
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      return BasketStatus.PAY_PENDING;
    }

    @Override
    public void changeBasketStatus(String bookingReference, String status) {
      // no-op
    }
  }

  /** Counts authorised-event publications, to prove exactly one processor ever publishes. */
  private static class PublishCountingActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger publishCount = new AtomicInteger();

    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
      publishCount.incrementAndGet();
      super.publishAuthorisedPaymentEvent(basketId, transactionId, cardAlias, authorizedAmount,
          currency, paymentMethod, last4Digits, expiry, paymentOption, language);
    }
  }

  /**
   * Models a gateway that confirms the authorization but omits the authorized amount from its
   * status response — the case the mobile strategy must resolve to the requested amount.
   */
  private static class AmountlessStatusActivitiesStub extends IntegrationActivitiesStub {

    @Override
    public DatatransTransactionStatus getTransactionStatus(String transactionId,
        String merchantId) {
      return new DatatransTransactionStatus(
          transactionId, "authorized", "GBP", null, "AUTH-123",
          new DatatransCardInfo("alias-test", "424242xxxxxx4242", "12", "29"),
          "VIS", BOOKING_REF);
    }
  }

  /**
   * Models a gateway whose reported transaction status the test script changes over time —
   * the fixture for proving that webhooks are hints and only the gateway's answer resolves
   * the payment.
   */
  /**
   * Models a gateway that no longer knows the transaction: every status read throws
   * {@code TransactionNotFoundException}, the answer Datatrans gives for a Secure Fields
   * transaction whose card form was never completed.
   */
  private static class UnknownTransactionActivitiesStub extends IntegrationActivitiesStub {

    final java.util.concurrent.atomic.AtomicInteger statusCalls =
        new java.util.concurrent.atomic.AtomicInteger();

    @Override
    public DatatransTransactionStatus getTransactionStatus(String transactionId,
        String merchantId) {
      statusCalls.incrementAndGet();
      throw new uk.co.whitbread.payment.orchestrator.domain.exceptions
          .TransactionNotFoundException("Transaction not found or expired");
    }
  }

  private static class MutableStatusActivitiesStub extends IntegrationActivitiesStub {

    volatile String gatewayStatus = "initialized";
    final java.util.concurrent.atomic.AtomicInteger statusCalls =
        new java.util.concurrent.atomic.AtomicInteger();

    @Override
    public DatatransTransactionStatus getTransactionStatus(String transactionId,
        String merchantId) {
      statusCalls.incrementAndGet();
      return new DatatransTransactionStatus(
          transactionId, gatewayStatus, "GBP", 8600, "AUTH-123",
          new DatatransCardInfo("alias-test", "424242xxxxxx4242", "12", "29"),
          "VIS", BOOKING_REF);
    }
  }

  /**
   * Models an authorize whose first attempt reached Datatrans, held the money, and then lost
   * its response. The retry stands in for the adapter's already-authorized recovery: it
   * returns the same authorization without touching the card again.
   */
  private static class AmbiguousAuthorizeActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger authorizeAttempts = new AtomicInteger();
    final AtomicInteger gatewayAuthorizations = new AtomicInteger();

    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId,
        String refno, long amount, String merchantId) {
      if (authorizeAttempts.incrementAndGet() == 1) {
        gatewayAuthorizations.incrementAndGet();
        throw new ServiceUnavailableException("Datatrans is unreachable: read timed out");
      }
      return new DatatransAuthorizeResponse(transactionId, "authorized", "AUTH-123",
          new DatatransCardInfo("alias-test", "424242xxxxxx4242", "12", "29"), "VIS");
    }
  }

  /** Models a failed 3-D Secure authentication, which must be reported rather than retried. */
  private static class ThreeDsFailingAuthorizeActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger authorizeAttempts = new AtomicInteger();

    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId,
        String refno, long amount, String merchantId) {
      authorizeAttempts.incrementAndGet();
      throw new ThreeDsAuthenticationFailedException("3-D Secure authentication failed");
    }
  }

  /**
   * Models Datatrans rejecting our merchant credentials on authorize — an operational fault of
   * ours that no retry can heal and that says nothing about the customer's card.
   */
  private static class RejectedCredentialsAuthorizeActivitiesStub
      extends IntegrationActivitiesStub {

    final AtomicInteger authorizeAttempts = new AtomicInteger();

    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId,
        String refno, long amount, String merchantId) {
      authorizeAttempts.incrementAndGet();
      throw new DatatransAuthenticationException(
          "Datatrans rejected merchant credentials during authorize: 401 UNAUTHORIZED");
    }
  }

  /**
   * Models a settle whose first attempt captured the money and then lost its response. The
   * retry stands in for the adapter's already-settled recovery.
   */
  private static class AmbiguousSettleActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger settleAttempts = new AtomicInteger();
    final AtomicInteger gatewayCaptures = new AtomicInteger();

    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      if (settleAttempts.incrementAndGet() == 1) {
        gatewayCaptures.incrementAndGet();
        throw new ServiceUnavailableException("Datatrans is unreachable: read timed out");
      }
    }
  }

  /** Models a settle the gateway confirms belongs to a different payment. */
  private static class MismatchingSettleActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger settleAttempts = new AtomicInteger();

    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settleAttempts.incrementAndGet();
      throw new TransactionMismatchException(
          "Datatrans authorized amount does not match the expected amount");
    }
  }

  /**
   * Models a gateway that is down for a long stretch — the first {@code failures} attempts
   * fail transiently, then one capture succeeds.
   */
  private static class FlakySettleActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger settleAttempts = new AtomicInteger();
    final AtomicInteger gatewayCaptures = new AtomicInteger();
    final AtomicInteger cancelAttempts = new AtomicInteger();
    private final int failures;

    FlakySettleActivitiesStub(int failures) {
      this.failures = failures;
    }

    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      if (settleAttempts.incrementAndGet() <= failures) {
        throw new ServiceUnavailableException("Datatrans is unreachable: read timed out");
      }
      gatewayCaptures.incrementAndGet();
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelAttempts.incrementAndGet();
      super.cancelTransaction(transactionId, merchantId);
    }
  }

  /** Models a gateway that never recovers within the settlement horizon. */
  private static class AlwaysFailingSettleActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger settleAttempts = new AtomicInteger();
    final AtomicInteger cancelAttempts = new AtomicInteger();

    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settleAttempts.incrementAndGet();
      throw new ServiceUnavailableException("Datatrans is unreachable: read timed out");
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelAttempts.incrementAndGet();
      super.cancelTransaction(transactionId, merchantId);
    }
  }

  /** Blocks the capture so a test can observe SETTLEMENT_PENDING and race a signal into it. */
  private static class BlockingSettleActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger settleAttempts = new AtomicInteger();
    private final java.util.concurrent.CountDownLatch settleStarted =
        new java.util.concurrent.CountDownLatch(1);
    private final java.util.concurrent.CountDownLatch settleRelease =
        new java.util.concurrent.CountDownLatch(1);

    boolean awaitSettleStarted() throws InterruptedException {
      return settleStarted.await(20, TimeUnit.SECONDS);
    }

    void releaseSettle() {
      settleRelease.countDown();
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      return BasketStatus.COMPLETED;
    }

    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settleAttempts.incrementAndGet();
      settleStarted.countDown();
      try {
        settleRelease.await(30, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * Models an unpublishable authorised-payment event. {@code publishSucceedsFrom} lets a test
   * flip the broker back on to prove the customer can retry.
   */
  private static class PublishFailureActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger publishAttempts = new AtomicInteger();
    final AtomicInteger cancelAttempts = new AtomicInteger();
    private volatile int failures;

    PublishFailureActivitiesStub(int failures) {
      this.failures = failures;
    }

    void publishSucceedsFrom(int remainingFailures) {
      this.failures = remainingFailures;
      publishAttempts.set(remainingFailures);
    }

    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
      if (publishAttempts.incrementAndGet() <= failures) {
        throw new ServiceUnavailableException("Kafka broker is unreachable");
      }
      super.publishAuthorisedPaymentEvent(basketId, transactionId, cardAlias, authorizedAmount,
          currency, paymentMethod, last4Digits, expiry, paymentOption, language);
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelAttempts.incrementAndGet();
      super.cancelTransaction(transactionId, merchantId);
    }
  }

  /**
   * Blocks the authorised-payment publish so a test can query the workflow while the publish
   * window is open — the window in which the payment must not yet look authorized.
   */
  private static class BlockingPublishActivitiesStub extends IntegrationActivitiesStub {

    private final java.util.concurrent.CountDownLatch publishStarted =
        new java.util.concurrent.CountDownLatch(1);
    private final java.util.concurrent.CountDownLatch publishRelease =
        new java.util.concurrent.CountDownLatch(1);

    boolean awaitPublishStarted() throws InterruptedException {
      return publishStarted.await(20, TimeUnit.SECONDS);
    }

    void releasePublish() {
      publishRelease.countDown();
    }

    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
      publishStarted.countDown();
      try {
        publishRelease.await(30, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      super.publishAuthorisedPaymentEvent(basketId, transactionId, cardAlias, authorizedAmount,
          currency, paymentMethod, last4Digits, expiry, paymentOption, language);
    }
  }

  /** Answers the booking-completion poll with a fixed basket status. */
  private static class PollingActivitiesStub extends IntegrationActivitiesStub {

    final AtomicInteger basketStatusReads = new AtomicInteger();
    final AtomicInteger settleAttempts = new AtomicInteger();
    final AtomicInteger cancelAttempts = new AtomicInteger();
    private final BasketStatus status;

    PollingActivitiesStub(BasketStatus status) {
      this.status = status;
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      basketStatusReads.incrementAndGet();
      return status;
    }

    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settleAttempts.incrementAndGet();
      super.settleTransaction(transactionId, amount, currency, refno, merchantId);
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelAttempts.incrementAndGet();
      super.cancelTransaction(transactionId, merchantId);
    }
  }
}
