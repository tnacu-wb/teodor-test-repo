package uk.co.whitbread.payment.orchestrator.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.failure.ApplicationFailure;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
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

/**
 * Comprehensive tests for the unified {@link PaymentWorkflowImpl} using Temporal's
 * {@link TestWorkflowExtension} for in-process workflow execution.
 *
 * <p>Tests cover both NEW_CARD_WEB and NEW_CARD_MOBILE flows through the single unified
 * workflow, including:
 * <ul>
 *   <li>Happy-path initialization and authorization for both methods</li>
 *   <li>Authorize @UpdateMethod pre-condition validation</li>
 *   <li>Secure webhook signal handling (transactionId validation before deposit)</li>
 *   <li>Re-initialization policy matrix with all state combinations</li>
 *   <li>Workflow expiry timeout behavior with authorization safety guard</li>
 *   <li>Command polymorphism and type discrimination</li>
 *   <li>Retry-after-failure scenarios with method switching</li>
 *   <li>Full reconciliation loop for mobile strategy</li>
 * </ul>
 */
class PaymentWorkflowImplTest {

  private static final String BASKET_ID = "basket-unified-001";
  private static final String TRANSACTION_ID = "txn-unified-abc123";
  private static final String MERCHANT_ID = "deWB-HOTEL-001";
  private static final String BOOKING_REFERENCE = "PI-123456789";
  private static final String CARD_ALIAS = "alias-unified-001";
  private static final String RETURN_URL = "https://example.com/return";

  @RegisterExtension
  static final TestWorkflowExtension testWorkflowExtension =
      TestWorkflowExtension.newBuilder()
          .setWorkflowTypes(PaymentWorkflowImpl.class)
          .setDoNotStart(true)
          .build();

  // ==========================================================================
  // NEW_CARD_WEB Happy Path
  // ==========================================================================

  @Nested
  class NewCardWebHappyPath {

    @Test
    void webInit_authorize_bookingCompleted_settles(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Step 1: Initialize with NEW_CARD_WEB command
      PaymentInitResult initResult = workflow.init(webCommand());

      assertThat(initResult.success()).isTrue();
      assertThat(initResult.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(initResult.errorCode()).isNull();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      // Step 2: Authorize (synchronous @UpdateMethod)
      AuthorizeResult authResult = workflow.authorize();

      assertThat(authResult.success()).isTrue();
      assertThat(authResult.errorCode()).isNull();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Step 3: BookingCompleted triggers settlement
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.getSettleCallCount()).isOne();
    }

    @Test
    void webInit_returnsTransactionIdOnSuccess(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      PaymentInitResult result = workflow.init(webCommand());

      assertThat(result.success()).isTrue();
      assertThat(result.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(result.errorCode()).isNull();
      assertThat(result.errorMessage()).isNull();
    }
  }

  // ==========================================================================
  // NEW_CARD_MOBILE Happy Path
  // ==========================================================================

  @Nested
  class NewCardMobileHappyPath {

    @Test
    void mobileInit_webhook_bookingCompleted_settles(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Step 1: Initialize with NEW_CARD_MOBILE command
      PaymentInitResult initResult = workflow.init(mobileCommand());

      assertThat(initResult.success()).isTrue();
      assertThat(initResult.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      // Step 2: Webhook received (authorized)
      workflow.webhookReceived(authorizedWebhook(TRANSACTION_ID));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Step 3: BookingCompleted triggers settlement
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.getSettleCallCount()).isOne();
    }

    @Test
    void mobileInit_whenGatewayFails_returnsErrorResultAndKeepsTheWorkflowRetryable(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // A failed mobile init is an answer, not a broken update: throwing out of the handler
      // would fail the update through a second error channel and end the workflow, so the
      // customer's retry would have to start a new one instead of re-initializing this one.
      var stub = new FailingMobileInitActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      PaymentInitResult failed = workflow.init(mobileCommand());

      assertThat(failed.success()).isFalse();
      assertThat(failed.transactionId()).isNull();
      assertThat(failed.errorCode()).isNotNull();

      // The workflow is still open and still INITIALIZED — nothing terminal was recorded.
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
      assertThat(WorkflowStub.fromTyped(workflow).getExecution()).isNotNull();

      // And the same workflow re-initializes.
      stub.setSuccessMode(true);
      PaymentInitResult retry = workflow.init(mobileCommand());

      assertThat(retry.success()).isTrue();
      assertThat(retry.transactionId()).isEqualTo(TRANSACTION_ID);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
      // No prior transaction existed, so the re-init had nothing to cancel at the gateway.
      assertThat(stub.getCancelCallCount()).isZero();
    }

    @Test
    void mobileInit_returnsTransactionIdOnSuccess(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      PaymentInitResult result = workflow.init(mobileCommand());

      assertThat(result.success()).isTrue();
      assertThat(result.transactionId()).isEqualTo(TRANSACTION_ID);
    }
  }

  // ==========================================================================
  // Authorize Pre-condition Validation
  // ==========================================================================

  @Nested
  class AuthorizePreConditions {

    @Test
    void authorize_withNullTransactionId_returnsError(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Authorize without calling init first — transactionId is null
      AuthorizeResult result = workflow.authorize();

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
      assertThat(result.errorMessage()).contains("not initialized");
    }

    @Test
    void authorize_whenAuthorizationAlreadyInProgress_returnsError(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) throws Exception {
      var stub = new SlowAuthorizeActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // Start first authorize in background
      CompletableFuture<AuthorizeResult> firstAuth = CompletableFuture.supplyAsync(
          workflow::authorize);

      // Wait for the first authorize to be in progress
      assertThat(stub.awaitAuthorizeCalled()).isTrue();

      // Second authorize while first is in progress
      AuthorizeResult secondResult = workflow.authorize();

      assertThat(secondResult.success()).isFalse();
      assertThat(secondResult.errorCode())
          .isEqualTo(PaymentErrorCode.AUTHORIZATION_IN_PROGRESS);

      // Release the first authorize
      stub.releaseAuthorize();
      AuthorizeResult firstResult = firstAuth.get(10, TimeUnit.SECONDS);
      assertThat(firstResult.success()).isTrue();
    }

    @Test
    void authorize_whenNotInInitializedState_returnsError(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // First authorize succeeds — moves to AUTHORIZED
      AuthorizeResult firstResult = workflow.authorize();
      assertThat(firstResult.success()).isTrue();

      // Second authorize when AUTHORIZED — not INITIALIZED
      AuthorizeResult secondResult = workflow.authorize();

      assertThat(secondResult.success()).isFalse();
      assertThat(secondResult.errorCode())
          .isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
      assertThat(secondResult.errorMessage()).contains("not in valid state");
    }
  }

  // ==========================================================================
  // Webhook Signal Handling (Secure Validation)
  // ==========================================================================

  @Nested
  class WebhookSignalHandling {

    @Test
    void webhook_withMismatchedTransactionId_isDropped(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(mobileCommand());

      // Send webhook with wrong transactionId — should be dropped
      workflow.webhookReceived(authorizedWebhook("txn-wrong-id"));

      // Workflow should still be INITIALIZED (webhook was dropped, not deposited)
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      // Now send the correct webhook — should be processed
      workflow.webhookReceived(authorizedWebhook(TRANSACTION_ID));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void webhook_withNullTransactionId_isDropped(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(mobileCommand());

      // Send webhook with null transactionId
      workflow.webhookReceived(authorizedWebhook(null));

      // Should still be INITIALIZED
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
    }

    @Test
    void webhook_beforeInit_isDropped(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Send webhook before init — state.transactionId is null, so it won't match
      workflow.webhookReceived(authorizedWebhook(TRANSACTION_ID));

      // Status remains INITIALIZED, no error
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
    }
  }

  // ==========================================================================
  // Re-initialization Policy Matrix
  // ==========================================================================

  @Nested
  class ReInitializationPolicyMatrix {

    @Test
    void reInit_whileInitialized_sameMethod_allowed(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // First init
      PaymentInitResult first = workflow.init(webCommand());
      assertThat(first.success()).isTrue();

      // Second init with same method while INITIALIZED — allowed
      PaymentInitResult second = workflow.init(webCommand());
      assertThat(second.success()).isTrue();
      assertThat(second.transactionId()).isEqualTo(TRANSACTION_ID);

      // Verify cancel was called for the first transaction
      assertThat(stub.getCancelCallCount()).isPositive();
    }

    @Test
    void reInit_whileInitialized_crossMethod_allowed(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // First init with web
      PaymentInitResult first = workflow.init(webCommand());
      assertThat(first.success()).isTrue();

      // Second init with mobile while INITIALIZED — allowed (cross-method)
      PaymentInitResult second = workflow.init(mobileCommand());
      assertThat(second.success()).isTrue();
      assertThat(second.transactionId()).isEqualTo(TRANSACTION_ID);

      // Cancel was called for prior transaction
      assertThat(stub.getCancelCallCount()).isPositive();
    }

    @Test
    void reInit_afterFailed_allowed(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new FailingThenSucceedingStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // First init succeeds
      PaymentInitResult first = workflow.init(webCommand());
      assertThat(first.success()).isTrue();

      // Authorize fails (strategy moves to FAILED)
      AuthorizeResult authResult = workflow.authorize();
      assertThat(authResult.success()).isFalse();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

      // Re-init after FAILED — allowed
      stub.setSuccessMode(true);
      PaymentInitResult reInit = workflow.init(webCommand());
      assertThat(reInit.success()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
    }

    @Test
    void reInit_whileAuthorized_rejected(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());
      workflow.authorize();

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Re-init while AUTHORIZED — rejected
      PaymentInitResult reInit = workflow.init(webCommand());
      assertThat(reInit.success()).isFalse();
      assertThat(reInit.errorCode()).isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
    }

    @Test
    void reInit_whileAuthorizationInProgress_rejected(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) throws Exception {
      var stub = new SlowAuthorizeActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // Start authorize in background
      CompletableFuture<AuthorizeResult> authFuture = CompletableFuture.supplyAsync(
          workflow::authorize);

      // Wait for authorization to be in progress
      assertThat(stub.awaitAuthorizeCalled()).isTrue();

      // Re-init while authorization is in progress — rejected
      PaymentInitResult reInit = workflow.init(webCommand());
      assertThat(reInit.success()).isFalse();
      assertThat(reInit.errorCode()).isEqualTo(PaymentErrorCode.AUTHORIZATION_IN_PROGRESS);

      // Clean up
      stub.releaseAuthorize();
      authFuture.get(10, TimeUnit.SECONDS);
    }
  }

  // ==========================================================================
  // Workflow Expiry Timeout
  // ==========================================================================

  @Nested
  class WorkflowExpiry {

    @Test
    void workflow_expiresAfterTimeout_whenNoActivity(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // Fast-forward time past the 30-minute expiry
      testEnv.sleep(Duration.ofMinutes(31));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.EXPIRED);
      // Cancel should have been called for the expired transaction
      assertThat(stub.getCancelCallCount()).isPositive();
    }

    @Test
    void workflow_honoursConfiguredTimeoutFromStartMemo(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      // Configured timeout is 5 minutes, well below the 30-minute default
      PaymentWorkflow workflow = startWorkflowWithExpiryTimeout(
          worker, client, Duration.ofMinutes(5));
      workflow.init(webCommand());

      testEnv.sleep(Duration.ofMinutes(6));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.EXPIRED);
    }

    @Test
    void workflow_expires_whenWebInitFailsAndNeverProducesTransaction(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      // The web strategy reports an init failure as an error result and leaves the payment
      // INITIALIZED with no transactionId. Nothing else can end the initialization wait, so the
      // expiry deadline has to.
      var stub = new FailingInitActivitiesStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflowWithExpiryTimeout(
          worker, client, Duration.ofMinutes(5));

      PaymentInitResult initResult = workflow.init(webCommand());
      assertThat(initResult.success()).isFalse();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);

      testEnv.sleep(Duration.ofMinutes(6));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.EXPIRED);
      // The failure was terminal for the activity — one attempt, no retries.
      assertThat(stub.getReservationCallCount()).isOne();
      // No transaction was ever created, so there was nothing to cancel at the gateway.
      assertThat(stub.getCancelCallCount()).isZero();
    }

    @Test
    void workflow_expires_whenInitNeverArrives(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflowWithExpiryTimeout(
          worker, client, Duration.ofMinutes(5));

      // No init at all — the workflow must not wait forever.
      testEnv.sleep(Duration.ofMinutes(6));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.EXPIRED);
      assertThat(stub.getCancelCallCount()).isZero();
    }

    @Test
    void workflow_doesNotExpire_whenInitSucceedsWithinWindow(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflowWithExpiryTimeout(
          worker, client, Duration.ofMinutes(5));

      // Init lands well inside the window, so the bounded Phase 1 await resolves normally.
      PaymentInitResult initResult = workflow.init(webCommand());
      assertThat(initResult.success()).isTrue();

      AuthorizeResult authResult = workflow.authorize();
      assertThat(authResult.success()).isTrue();

      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
      assertThat(stub.getSettleCallCount()).isOne();
    }

    @Test
    void workflow_doesNotExpire_whileSettled(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());
      workflow.authorize();
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);

      // Settled before expiry
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
    }
  }

  // ==========================================================================
  // Command Polymorphism
  // ==========================================================================

  @Nested
  class CommandPolymorphism {

    @Test
    void webCommand_usesSecureFieldsStrategy(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // Secure Fields was called (not Mobile SDK)
      assertThat(stub.getSecureFieldsCallCount()).isPositive();
      assertThat(stub.getMobileSdkCallCount()).isZero();
    }

    @Test
    void mobileCommand_usesMobileSdkStrategy(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(mobileCommand());

      // Mobile SDK was called (not Secure Fields)
      assertThat(stub.getMobileSdkCallCount()).isPositive();
      assertThat(stub.getSecureFieldsCallCount()).isZero();
    }
  }

  // ==========================================================================
  // Strategy Command-Type Guards
  // ==========================================================================

  @Nested
  class StrategyCommandTypeGuards {

    @Test
    void webStrategy_withWrongCommandType_returnsValidationFailed() {
      // A blind cast here would throw out of the init() update handler and leave the workflow
      // waiting on an initialization that can never arrive.
      PaymentInitResult result = new NewCardWebStrategy()
          .init(mobileCommand(), null, new WorkflowState());

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.VALIDATION_FAILED);
      assertThat(result.errorMessage()).contains("Expected NewCardWebInitCommand but received");
      assertThat(result.errorMessage()).contains("NewCardMobileInitCommand");
    }

    @Test
    void mobileStrategy_withWrongCommandType_returnsValidationFailed() {
      PaymentInitResult result = new NewCardMobileStrategy()
          .init(webCommand(), null, new WorkflowState());

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.VALIDATION_FAILED);
      assertThat(result.errorMessage()).contains("Expected NewCardMobileInitCommand but received");
    }
  }

  // ==========================================================================
  // Retry After Failure with Method Switching
  // ==========================================================================

  @Nested
  class RetryAfterFailure {

    @Test
    void afterWebFailure_reInitWithMobile_succeeds(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new FailingThenSucceedingStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Init web — succeeds
      PaymentInitResult webInit = workflow.init(webCommand());
      assertThat(webInit.success()).isTrue();

      // Authorize web — fails
      AuthorizeResult webAuth = workflow.authorize();
      assertThat(webAuth.success()).isFalse();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);

      // Re-init with mobile after failure — allowed (method switching)
      stub.setSuccessMode(true);
      PaymentInitResult mobileInit = workflow.init(mobileCommand());
      assertThat(mobileInit.success()).isTrue();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
    }
  }

  // ==========================================================================
  // Reconciliation Loop (Mobile)
  // ==========================================================================

  @Nested
  class MobileReconciliation {

    @Test
    void reconciliationPolling_authorizesViaPoll_whenNoWebhook(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = new ReconciliationStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Init with reconciliation enabled, very short timings for test
      var command = new NewCardMobileInitCommand(
          BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://example.com/webhooks/datatrans?basketId=" + BASKET_ID,
          new MobileSdkReconciliationSettings(true, 1, 1, 60_000));

      PaymentInitResult initResult = workflow.init(command);
      assertThat(initResult.success()).isTrue();

      // Advance time past initial delay + poll interval to trigger reconciliation
      testEnv.sleep(Duration.ofMillis(5));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(stub.getTransactionStatusCallCount()).isPositive();

      // Complete the booking
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      WorkflowStub.fromTyped(workflow).getResult(Void.class);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
    }

    @Test
    void webhook_arrivesBeforeReconciliation_takePrecedence(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultMobileStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);

      // Init with reconciliation enabled but long initial delay
      var command = new NewCardMobileInitCommand(
          BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
          "https://example.com/webhooks/datatrans?basketId=" + BASKET_ID,
          new MobileSdkReconciliationSettings(true, 120_000, 30_000, 300_000));

      workflow.init(command);

      // Webhook arrives before reconciliation starts
      workflow.webhookReceived(authorizedWebhook(TRANSACTION_ID));

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Complete
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      WorkflowStub.fromTyped(workflow).getResult(Void.class);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.SETTLED);
    }
  }

  // ==========================================================================
  // Synchronous Authorization Response (no polling)
  // ==========================================================================

  @Nested
  class SynchronousAuthorization {

    @Test
    void authorize_returnsSynchronousResult(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // Authorize returns immediately without polling
      AuthorizeResult result = workflow.authorize();

      assertThat(result).isNotNull();
      assertThat(result.success()).isTrue();
      assertThat(result.errorCode()).isNull();
    }
  }

  // ==========================================================================
  // BookingCompleted Signal Handling
  // ==========================================================================

  @Nested
  class BookingCompletedHandling {

    @Test
    void bookingCompleted_failed_cancelsPayment(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());
      workflow.authorize();
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);

      // Booking failed — payment should be cancelled
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "FAILED"));

      WorkflowStub.fromTyped(workflow).getResult(Void.class);
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
      assertThat(stub.getCancelCallCount()).isPositive();
    }

    @Test
    void bookingCompleted_inNonAuthorizedState_isIgnored(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      var stub = defaultWebStub();
      worker.registerActivitiesImplementations(stub);
      testEnv.start();

      PaymentWorkflow workflow = startWorkflow(worker, client);
      workflow.init(webCommand());

      // BookingCompleted while INITIALIZED — should be ignored
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));

      // Status unchanged
      assertThat(workflow.getPaymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
    }
  }

  // ==========================================================================
  // Helpers — Workflow Start
  // ==========================================================================

  private static PaymentWorkflow startWorkflow(Worker worker, WorkflowClient client) {
    PaymentWorkflow workflow = client.newWorkflowStub(
        PaymentWorkflow.class,
        WorkflowOptions.newBuilder().setTaskQueue(worker.getTaskQueue()).build());
    WorkflowClient.start(workflow::run, BASKET_ID);
    return workflow;
  }

  /**
   * Starts the workflow carrying the expiry-timeout start memo the adapter sets from
   * {@code integrations.payment.workflow.timeout}.
   */
  private static PaymentWorkflow startWorkflowWithExpiryTimeout(
      Worker worker, WorkflowClient client, Duration timeout) {
    PaymentWorkflow workflow = client.newWorkflowStub(
        PaymentWorkflow.class,
        WorkflowOptions.newBuilder()
            .setTaskQueue(worker.getTaskQueue())
            .setMemo(Map.of(PaymentWorkflowImpl.EXPIRY_TIMEOUT_MEMO_KEY, timeout.toMillis()))
            .build());
    WorkflowClient.start(workflow::run, BASKET_ID);
    return workflow;
  }

  // ==========================================================================
  // Helpers — Command Factories
  // ==========================================================================

  private static NewCardWebInitCommand webCommand() {
    return new NewCardWebInitCommand(BASKET_ID, RETURN_URL, "gb", "en", "LEISURE", "PI");
  }

  private static NewCardMobileInitCommand mobileCommand() {
    return new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
        "https://example.com/webhooks/datatrans?basketId=" + BASKET_ID, null);
  }

  // ==========================================================================
  // Helpers — Webhook Payloads
  // ==========================================================================

  private static DatatransWebhookPayload authorizedWebhook(String transactionId) {
    return new DatatransWebhookPayload(
        transactionId, MERCHANT_ID, "authorized", "GBP", BOOKING_REFERENCE,
        "VIS", 8600, CARD_ALIAS, "424242xxxxxx4242", "12", "29", "AUTH-123");
  }

  // ==========================================================================
  // Helpers — Status Polling
  // ==========================================================================

  private static void awaitPaymentStatus(PaymentWorkflow workflow, PaymentStatus expected) {
    long deadline = System.currentTimeMillis() + 10_000;
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
  // Stubs — Default Web Activities
  // ==========================================================================

  private static UnifiedPaymentActivitiesStub defaultWebStub() {
    return new UnifiedPaymentActivitiesStub();
  }

  private static UnifiedPaymentActivitiesStub defaultMobileStub() {
    return new UnifiedPaymentActivitiesStub();
  }

  /**
   * Comprehensive PaymentActivities stub for unified workflow testing.
   * Supports both web and mobile flows through a single stub instance.
   */
  static class UnifiedPaymentActivitiesStub implements PaymentActivities {

    private final AtomicInteger secureFieldsCallCount = new AtomicInteger();
    private final AtomicInteger mobileSdkCallCount = new AtomicInteger();
    private final AtomicInteger settleCallCount = new AtomicInteger();
    private final AtomicInteger cancelCallCount = new AtomicInteger();
    private final AtomicInteger transactionStatusCallCount = new AtomicInteger();


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
        long amount, String merchantId) {
      return new DatatransAuthorizeResponse(transactionId, "authorized", "AUTH-123",
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS");
    }


    @Override
    public String resolveMerchantId(String hotelCode) {
      return "deWB-" + hotelCode;
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelCallCount.incrementAndGet();
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      return BasketStatus.PAY_PENDING;
    }

    @Override
    public void changeBasketStatus(String bookingReference, String status) {
    }

    @Override
    public Reservation getReservation(String basketId) {
      return new Reservation(basketId, "HOTEL-001", new BigDecimal("86.00"),
          "GBP", BOOKING_REFERENCE, BOOKING_REFERENCE, "PI");
    }

    @Override
    public PaymentMethodValidationResult validatePaymentMethods(
        String basketReference, String hotelId, String country, String language,
        String userType, String clientChannel) {
      return new PaymentMethodValidationResult(true, List.of("VIS", "ECA"));
    }

    @Override
    public String initDatatransSecureFields(DatatransSecureFieldsRequest request) {
      secureFieldsCallCount.incrementAndGet();
      return TRANSACTION_ID;
    }

    @Override
    public String initMobileSdkTransaction(DatatransMobileSdkRequest request) {
      mobileSdkCallCount.incrementAndGet();
      return TRANSACTION_ID;
    }

    @Override
    public DatatransTransactionStatus getTransactionStatus(String transactionId,
        String merchantId) {
      transactionStatusCallCount.incrementAndGet();
      return new DatatransTransactionStatus(
          transactionId, "authorized", "GBP", 8600, "AUTH-123",
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS", null);
    }


    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settleCallCount.incrementAndGet();
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
    }

    int getSecureFieldsCallCount() {
      return secureFieldsCallCount.get();
    }

    int getMobileSdkCallCount() {
      return mobileSdkCallCount.get();
    }

    int getSettleCallCount() {
      return settleCallCount.get();
    }

    int getCancelCallCount() {
      return cancelCallCount.get();
    }

    int getTransactionStatusCallCount() {
      return transactionStatusCallCount.get();
    }
  }

  // ==========================================================================
  // Stubs — Slow Authorize (for concurrent tests)
  // ==========================================================================

  /**
   * Activities stub that blocks on authorizeTransaction until explicitly released.
   * Used to test concurrent authorization pre-condition validation.
   */
  static class SlowAuthorizeActivitiesStub implements PaymentActivities {

    private final java.util.concurrent.CountDownLatch authorizeCalled =
        new java.util.concurrent.CountDownLatch(1);
    private final java.util.concurrent.CountDownLatch authorizeRelease =
        new java.util.concurrent.CountDownLatch(1);

    boolean awaitAuthorizeCalled() throws InterruptedException {
      return authorizeCalled.await(10, TimeUnit.SECONDS);
    }

    void releaseAuthorize() {
      authorizeRelease.countDown();
    }


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
        long amount, String merchantId) {
      authorizeCalled.countDown();
      try {
        authorizeRelease.await(30, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return new DatatransAuthorizeResponse(transactionId, "authorized", "AUTH-123",
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS");
    }


    @Override
    public String resolveMerchantId(String hotelCode) {
      return "deWB-" + hotelCode;
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      return BasketStatus.PAY_PENDING;
    }

    @Override
    public void changeBasketStatus(String bookingReference, String status) {
    }

    @Override
    public Reservation getReservation(String basketId) {
      return new Reservation(basketId, "HOTEL-001", new BigDecimal("86.00"),
          "GBP", BOOKING_REFERENCE, BOOKING_REFERENCE, "PI");
    }

    @Override
    public PaymentMethodValidationResult validatePaymentMethods(
        String basketReference, String hotelId, String country, String language,
        String userType, String clientChannel) {
      return new PaymentMethodValidationResult(true, List.of("VIS", "ECA"));
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
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS", null);
    }


    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
    }
  }

  // ==========================================================================
  // Stubs — Failing Then Succeeding (for retry/re-init tests)
  // ==========================================================================

  /**
   * Activities stub that fails authorize initially, then succeeds after setSuccessMode(true).
   * Used to test re-initialization after failure and method switching.
   */
  static class FailingThenSucceedingStub implements PaymentActivities {

    private volatile boolean successMode;
    private final AtomicInteger cancelCallCount = new AtomicInteger();

    void setSuccessMode(boolean successMode) {
      this.successMode = successMode;
    }


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
        long amount, String merchantId) {
      if (!successMode) {
        throw new RuntimeException("Authorization declined");
      }
      return new DatatransAuthorizeResponse(transactionId, "authorized", "AUTH-123",
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS");
    }


    @Override
    public String resolveMerchantId(String hotelCode) {
      return "deWB-" + hotelCode;
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      cancelCallCount.incrementAndGet();
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      return BasketStatus.PAY_PENDING;
    }

    @Override
    public void changeBasketStatus(String bookingReference, String status) {
    }

    @Override
    public Reservation getReservation(String basketId) {
      return new Reservation(basketId, "HOTEL-001", new BigDecimal("86.00"),
          "GBP", BOOKING_REFERENCE, BOOKING_REFERENCE, "PI");
    }

    @Override
    public PaymentMethodValidationResult validatePaymentMethods(
        String basketReference, String hotelId, String country, String language,
        String userType, String clientChannel) {
      return new PaymentMethodValidationResult(true, List.of("VIS", "ECA"));
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
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS", null);
    }


    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
    }
  }

  // ==========================================================================
  // Stubs — Failing Init (for the never-initialized expiry test)
  // ==========================================================================

  /**
   * Activities stub whose reservation lookup fails terminally, so the web strategy's init
   * returns an error result without ever producing a transactionId.
   */
  static class FailingInitActivitiesStub extends UnifiedPaymentActivitiesStub {

    private final AtomicInteger reservationCallCount = new AtomicInteger();

    @Override
    public Reservation getReservation(String basketId) {
      reservationCallCount.incrementAndGet();
      throw ApplicationFailure.newNonRetryableFailure(
          "Reservation lookup failed", "ReservationLookupFailure");
    }

    int getReservationCallCount() {
      return reservationCallCount.get();
    }
  }

  // ==========================================================================
  // Stubs — Failing Mobile Init (for the mobile init-failure contract)
  // ==========================================================================

  /**
   * Activities stub whose Mobile SDK init fails terminally until {@code setSuccessMode(true)},
   * so the mobile strategy's init returns an error result without producing a transactionId.
   */
  static class FailingMobileInitActivitiesStub extends UnifiedPaymentActivitiesStub {

    private volatile boolean successMode;

    void setSuccessMode(boolean successMode) {
      this.successMode = successMode;
    }

    @Override
    public String initMobileSdkTransaction(DatatransMobileSdkRequest request) {
      if (!successMode) {
        throw ApplicationFailure.newNonRetryableFailure(
            "Datatrans rejected the Mobile SDK init", "MobileSdkInitFailure");
      }
      return super.initMobileSdkTransaction(request);
    }
  }

  // ==========================================================================
  // Stubs — Reconciliation (for polling tests)
  // ==========================================================================

  /**
   * Activities stub that returns authorized status on transaction status poll.
   * Used to test the full reconciliation loop for mobile strategy.
   */
  static class ReconciliationStub implements PaymentActivities {

    private final AtomicInteger transactionStatusCallCount = new AtomicInteger();
    private final AtomicInteger settleCallCount = new AtomicInteger();


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
        long amount, String merchantId) {
      return new DatatransAuthorizeResponse(transactionId, "authorized", "AUTH-123",
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS");
    }


    @Override
    public String resolveMerchantId(String hotelCode) {
      return "deWB-" + hotelCode;
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
    }

    @Override
    public BasketStatus getBasketStatus(String basketId) {
      return BasketStatus.PAY_PENDING;
    }

    @Override
    public void changeBasketStatus(String bookingReference, String status) {
    }

    @Override
    public Reservation getReservation(String basketId) {
      return new Reservation(basketId, "HOTEL-001", new BigDecimal("86.00"),
          "GBP", BOOKING_REFERENCE, BOOKING_REFERENCE, "APPS_IOS");
    }

    @Override
    public PaymentMethodValidationResult validatePaymentMethods(
        String basketReference, String hotelId, String country, String language,
        String userType, String clientChannel) {
      return new PaymentMethodValidationResult(true, List.of("VIS", "ECA"));
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
      transactionStatusCallCount.incrementAndGet();
      return new DatatransTransactionStatus(
          transactionId, "authorized", "GBP", 8600, "AUTH-123",
          new DatatransCardInfo(CARD_ALIAS, "424242xxxxxx4242", "12", "29"), "VIS", null);
    }


    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      settleCallCount.incrementAndGet();
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
    }

    int getTransactionStatusCallCount() {
      return transactionStatusCallCount.get();
    }

    int getSettleCallCount() {
      return settleCallCount.get();
    }
  }
}
