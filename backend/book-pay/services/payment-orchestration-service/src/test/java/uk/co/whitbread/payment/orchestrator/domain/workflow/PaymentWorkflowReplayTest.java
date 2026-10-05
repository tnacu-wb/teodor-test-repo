package uk.co.whitbread.payment.orchestrator.domain.workflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.WorkflowExecutionHistory;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.testing.WorkflowReplayer;
import io.temporal.worker.Worker;
import java.math.BigDecimal;
import java.util.List;
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
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

/**
 * Replay tests verifying workflow determinism using Temporal's {@link WorkflowReplayer}.
 *
 * <p>These tests record a workflow execution via {@link TestWorkflowEnvironment}, extract
 * the event history, and replay it against the current {@link PaymentWorkflowImpl} to validate
 * that workflow code changes remain compatible with existing histories.
 *
 * <h2>Strategy</h2>
 * <p>Instead of depending on pre-recorded static history files (which would break on every
 * workflow code change during development), these tests:
 * <ol>
 *   <li>Execute a full workflow in a test environment to produce a recorded history</li>
 *   <li>Replay that history against the same workflow class to verify determinism</li>
 *   <li>Validate that {@link PaymentMethodStrategyFactory} produces deterministic results</li>
 * </ol>
 *
 * <h2>Workflow.getVersion() Policy</h2>
 * <p>When future changes to the workflow spine are needed (e.g. adding new steps between
 * existing ones), the pattern is:
 * <pre>{@code
 * int version = Workflow.getVersion("add-loyalty-points", Workflow.DEFAULT_VERSION, 1);
 * if (version >= 1) {
 *     activities.addLoyaltyPoints(...);
 * }
 * }</pre>
 * <p>Replay tests will then need both old and new histories to verify backward compatibility.
 * The recorded history approach in this class supports that by allowing multiple history files.
 *
 * <p>Validates: Requirements 17.1, 17.2, 17.3, 17.4, 17.5
 */
class PaymentWorkflowReplayTest {

  private static final String BASKET_ID = "ARH-replay-test-001";
  private static final String TRANSACTION_ID = "txn-replay-12345";
  private static final String MERCHANT_ID = "deWB-HARHOR";
  private static final String BOOKING_REF = "ARH9876543";

  @RegisterExtension
  static final TestWorkflowExtension testWorkflowExtension =
      TestWorkflowExtension.newBuilder()
          .setWorkflowTypes(PaymentWorkflowImpl.class)
          .setDoNotStart(true)
          .build();

  @Nested
  class StrategyFactoryDeterminism {

    @Test
    void factoryProducesSameStrategyTypeForNewCardWeb() {
      PaymentMethodStrategy strategy1 = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_WEB);
      PaymentMethodStrategy strategy2 = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_WEB);

      assertThat(strategy1.getClass()).isEqualTo(strategy2.getClass());
      assertThat(strategy1.getSupportedMethod()).isEqualTo(strategy2.getSupportedMethod());
      assertThat(strategy1.getSupportedMethod()).isEqualTo(PaymentMethod.NEW_CARD_WEB);
    }

    @Test
    void factoryProducesSameStrategyTypeForNewCardMobile() {
      PaymentMethodStrategy strategy1 = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_MOBILE);
      PaymentMethodStrategy strategy2 = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_MOBILE);

      assertThat(strategy1.getClass()).isEqualTo(strategy2.getClass());
      assertThat(strategy1.getSupportedMethod()).isEqualTo(strategy2.getSupportedMethod());
      assertThat(strategy1.getSupportedMethod()).isEqualTo(PaymentMethod.NEW_CARD_MOBILE);
    }

    @Test
    void factoryIsStatelessAcrossMultipleCalls() {
      // Verify factory produces correct strategy regardless of call order
      PaymentMethodStrategy web = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_WEB);
      PaymentMethodStrategy mobile = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_MOBILE);
      PaymentMethodStrategy webAgain = PaymentMethodStrategyFactory.create(
          PaymentMethod.NEW_CARD_WEB);

      assertThat(web.getClass()).isEqualTo(webAgain.getClass());
      assertThat(web.getClass()).isNotEqualTo(mobile.getClass());
      assertThat(web).isInstanceOf(NewCardWebStrategy.class);
      assertThat(mobile).isInstanceOf(NewCardMobileStrategy.class);
    }

    @Test
    void allEnumValuesHaveCorrespondingStrategy() {
      // Ensure every PaymentMethod value has a strategy — prevents replay failures
      // when a new enum value is added without a corresponding strategy
      for (PaymentMethod method : PaymentMethod.values()) {
        assertThatCode(() -> PaymentMethodStrategyFactory.create(method))
            .as("Strategy for " + method)
            .doesNotThrowAnyException();
      }
    }
  }

  @Nested
  class NewCardWebReplay {

    @Test
    void replayNewCardWebInitAndAuthorize(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      worker.registerActivitiesImplementations(new ReplayActivitiesStub());
      testEnv.start();

      String workflowId = "payment-" + BASKET_ID;

      // Execute workflow with a deterministic workflow ID to produce a history
      PaymentWorkflow workflow = client.newWorkflowStub(
          PaymentWorkflow.class,
          WorkflowOptions.newBuilder()
              .setWorkflowId(workflowId)
              .setTaskQueue(worker.getTaskQueue())
              .build());

      WorkflowClient.start(workflow::run, BASKET_ID);

      PaymentInitResult initResult = workflow.init(
          new NewCardWebInitCommand(BASKET_ID, "https://premierinn.com/return",
              "gb", "en", "LEISURE", "PI"));
      assertThat(initResult.success()).isTrue();

      AuthorizeResult authorizeResult = workflow.authorize();
      assertThat(authorizeResult.success()).isTrue();

      // Signal booking completed to drive workflow to terminal state
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);

      // Extract the recorded history
      WorkflowExecutionHistory history = client.fetchHistory(workflowId);

      // Replay against the same workflow class — verifies determinism
      assertThatCode(() -> WorkflowReplayer.replayWorkflowExecution(
          history, PaymentWorkflowImpl.class))
          .as("Replay of NEW_CARD_WEB init+authorize+settle flow should not throw")
          .doesNotThrowAnyException();
    }
  }

  @Nested
  class NewCardMobileReplay {

    @Test
    void replayNewCardMobileInitAndWebhook(
        TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
      worker.registerActivitiesImplementations(new ReplayActivitiesStub());
      testEnv.start();

      String workflowId = "payment-" + BASKET_ID;

      PaymentWorkflow workflow = client.newWorkflowStub(
          PaymentWorkflow.class,
          WorkflowOptions.newBuilder()
              .setWorkflowId(workflowId)
              .setTaskQueue(worker.getTaskQueue())
              .build());

      WorkflowClient.start(workflow::run, BASKET_ID);

      var reconciliation = new MobileSdkReconciliationSettings(
          true, 5000, 10000, 300000);
      PaymentInitResult initResult = workflow.init(
          new NewCardMobileInitCommand(BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS",
              "https://callbacks.premierinn.com/webhooks/datatrans?basketId=" + BASKET_ID,
              reconciliation));
      assertThat(initResult.success()).isTrue();

      // Send webhook
      var webhookPayload = new DatatransWebhookPayload(
          TRANSACTION_ID, MERCHANT_ID, "authorized", "GBP",
          BOOKING_REF, "VIS", 8600, "card-alias-replay",
          "424242xxxxxx4242", "12", "29", "AUTH-REPLAY");
      workflow.webhookReceived(webhookPayload);

      awaitPaymentStatus(workflow, PaymentStatus.AUTHORIZED);

      // Settle
      workflow.bookingCompleted(new BookingCompletedEvent(BASKET_ID, "COMPLETED"));
      awaitPaymentStatus(workflow, PaymentStatus.SETTLED);

      // Extract and replay
      WorkflowExecutionHistory history = client.fetchHistory(workflowId);

      assertThatCode(() -> WorkflowReplayer.replayWorkflowExecution(
          history, PaymentWorkflowImpl.class))
          .as("Replay of NEW_CARD_MOBILE init+webhook+settle flow should not throw")
          .doesNotThrowAnyException();
    }
  }

  @Nested
  class WorkflowVersioningPolicy {

    @Test
    void workflowSourceUsesTemporalDeterministicApisOnly() throws Exception {
      // Static analysis to verify workflow code doesn't use non-deterministic APIs.
      // This guards against accidental introduction of non-deterministic code that
      // would break replay. See Temporal SDK determinism requirements.
      String workflowSource = readWorkflowImplSource();

      assertThat(workflowSource)
          .doesNotContain("System.currentTimeMillis(")
          .doesNotContain("Instant.now(")
          .doesNotContain("Thread.sleep(")
          .doesNotContain("java.util.Random")
          .doesNotContain("UUID.randomUUID(")
          .doesNotContain("CompletableFuture")
          .doesNotContain("new Thread(");
    }

    @Test
    void strategyFactorySourceIsDeterministic() throws Exception {
      // Verify the factory source uses no non-deterministic logic
      String factorySource = readFactorySource();

      assertThat(factorySource)
          .doesNotContain("System.currentTimeMillis(")
          .doesNotContain("Random")
          .doesNotContain("UUID.randomUUID(")
          .doesNotContain("Math.random(");
    }
  }

  // ==========================================================================
  // Helpers
  // ==========================================================================

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

  private static String readWorkflowImplSource() throws Exception {
    return readSourceFile("PaymentWorkflowImpl.java");
  }

  private static String readFactorySource() throws Exception {
    return readSourceFile("PaymentMethodStrategyFactory.java");
  }

  private static String readSourceFile(String fileName) throws Exception {
    String modulePath =
        "src/main/java/uk/co/whitbread/payment/orchestrator/domain/workflow/" + fileName;
    String servicePath =
        "backend/book-pay/services/payment-orchestration-service/" + modulePath;

    java.nio.file.Path workingDir = java.nio.file.Path.of("").toAbsolutePath();
    for (java.nio.file.Path dir = workingDir; dir != null; dir = dir.getParent()) {
      java.nio.file.Path candidate = dir.resolve(modulePath);
      if (java.nio.file.Files.exists(candidate)) {
        return java.nio.file.Files.readString(candidate);
      }
      candidate = dir.resolve(servicePath);
      if (java.nio.file.Files.exists(candidate)) {
        return java.nio.file.Files.readString(candidate);
      }
    }
    throw new java.io.IOException("Could not locate " + fileName);
  }

  // ==========================================================================
  // Activity Stub
  // ==========================================================================

  /**
   * Activity stub for replay tests. Must produce consistent, predictable results
   * because the same activities will be replayed during the replay verification phase.
   */
  private static class ReplayActivitiesStub implements PaymentActivities {


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId,
        String refno, long amount, String merchantId) {
      return new DatatransAuthorizeResponse(
          transactionId, "authorized", "AUTH-REPLAY",
          new DatatransCardInfo("alias-replay", "424242xxxxxx4242", "12", "29"),
          "VIS");
    }


    @Override
    public Reservation getReservation(String basketId) {
      return new Reservation(BASKET_ID, "HARHOR", new BigDecimal("86.00"),
          "GBP", BOOKING_REF, BOOKING_REF, "PI");
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
          transactionId, "authorized", "GBP", 8600, "AUTH-REPLAY",
          new DatatransCardInfo("alias-replay", "424242xxxxxx4242", "12", "29"),
          "VIS", BOOKING_REF);
    }


    @Override
    public void settleTransaction(String transactionId, long amount, String currency,
        String refno, String merchantId) {
      // no-op
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
      // no-op
    }

    @Override
    public void cancelTransaction(String transactionId, String merchantId) {
      // no-op
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
}
