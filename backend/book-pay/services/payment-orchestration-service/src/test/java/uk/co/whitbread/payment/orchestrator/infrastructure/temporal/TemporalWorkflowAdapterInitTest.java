package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import static org.assertj.core.api.Assertions.assertThat;

import io.temporal.client.WorkflowClient;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.testing.TestWorkflowExtension;
import io.temporal.worker.Worker;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransAuthorizeResponse;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransSecureFieldsRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardMobileInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardWebInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentActivities;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentWorkflowImpl;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransReconciliationProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransWebhookProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentWorkflowProperties;

class TemporalWorkflowAdapterInitTest {

  private static final String BASKET_ID = "basket-789";
  private static final String RETURN_URL = "https://example.com/return";

  @RegisterExtension
  static final TestWorkflowExtension testWorkflowExtension =
      TestWorkflowExtension.newBuilder()
          .setWorkflowTypes(PaymentWorkflowImpl.class)
          .setDoNotStart(true)
          .build();

  // --- NewCardWeb tests ---

  @Test
  void initPayment_webCommand_returnsResultViaUpdateWithStart(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    var underTest = createAdapter(client, worker.getTaskQueue());

    var command = new NewCardWebInitCommand(
        BASKET_ID, RETURN_URL, "gb", "en", "LEISURE", "PI");

    PaymentInitResult result = underTest.initPayment(command);

    assertThat(result.success()).isTrue();
    assertThat(result.transactionId()).isEqualTo("txn-update-with-start");
  }

  @Test
  void initPayment_webCommand_reusesExistingWorkflowWithUseExistingPolicy(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    var underTest = createAdapter(client, worker.getTaskQueue());

    var command = new NewCardWebInitCommand(
        BASKET_ID, RETURN_URL, "gb", "en", "LEISURE", "PI");

    PaymentInitResult firstResult = underTest.initPayment(command);
    PaymentInitResult secondResult = underTest.initPayment(command);

    assertThat(firstResult.success()).isTrue();
    assertThat(secondResult.success()).isTrue();
    // Re-init before authorize calls Datatrans again (no caching)
    assertThat(activities.getDatatransCallCount()).isEqualTo(2);
  }

  // --- NewCardMobile tests ---

  @Test
  void initPayment_mobileCommand_enrichesWithWebhookUrlAndReconciliation(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    DatatransWebhookProperties webhookProperties = new DatatransWebhookProperties();
    webhookProperties.setCallbackBaseUrl("https://api.premierinn.com/api/payments/webhooks");
    DatatransReconciliationProperties reconciliationProperties =
        new DatatransReconciliationProperties();
    reconciliationProperties.setInitialDelay(Duration.ofMinutes(2));
    reconciliationProperties.setPollInterval(Duration.ofSeconds(30));
    reconciliationProperties.setMaxDuration(Duration.ofMinutes(30));

    var underTest = new TemporalWorkflowAdapter(
        client, webhookProperties, reconciliationProperties,
        new PaymentWorkflowProperties(), worker.getTaskQueue());

    var command = new NewCardMobileInitCommand(
        BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS");

    PaymentInitResult result = underTest.initPayment(command);

    // Verify the adapter successfully initializes via the unified workflow
    assertThat(result.success()).isTrue();
    assertThat(result.transactionId()).isEqualTo("mobile-txn-stub");
    // Verify the mobile SDK init activity was invoked (confirming mobile strategy was used)
    assertThat(activities.getMobileSdkCallCount()).isEqualTo(1);
  }

  @Test
  void initPayment_mobileCommand_sendsRenamedWebhookUrlToDatatrans(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    DatatransWebhookProperties webhookProperties = new DatatransWebhookProperties();
    webhookProperties.setCallbackBaseUrl("https://api.premierinn.com/api/payments/webhooks");
    DatatransReconciliationProperties reconciliationProperties =
        new DatatransReconciliationProperties();
    reconciliationProperties.setInitialDelay(Duration.ofMinutes(2));
    reconciliationProperties.setPollInterval(Duration.ofSeconds(30));
    reconciliationProperties.setMaxDuration(Duration.ofMinutes(30));

    var underTest = new TemporalWorkflowAdapter(
        client, webhookProperties, reconciliationProperties,
        new PaymentWorkflowProperties(), worker.getTaskQueue());

    var command = new NewCardMobileInitCommand(
        BASKET_ID, "gb", "en", "LEISURE", "APPS_IOS");

    PaymentInitResult result = underTest.initPayment(command);

    assertThat(result.success()).isTrue();
    assertThat(activities.getCapturedMobileSdkRequest().webhookUrl())
        .isEqualTo("https://api.premierinn.com/api/payments/webhooks/datatrans?basketId="
            + BASKET_ID);
  }

  @Test
  void toReconciliationSettings_mapsPropertiesCorrectly() {
    DatatransReconciliationProperties properties = new DatatransReconciliationProperties();
    properties.setInitialDelay(Duration.ofMinutes(2));
    properties.setPollInterval(Duration.ofSeconds(30));
    properties.setMaxDuration(Duration.ofMinutes(30));
    properties.setEnabled(true);

    var settings = TemporalWorkflowAdapter.toReconciliationSettings(properties);

    assertThat(settings.enabled()).isTrue();
    assertThat(settings.initialDelayMillis()).isEqualTo(Duration.ofMinutes(2).toMillis());
    assertThat(settings.pollIntervalMillis()).isEqualTo(Duration.ofSeconds(30).toMillis());
    assertThat(settings.maxDurationMillis()).isEqualTo(Duration.ofMinutes(30).toMillis());
  }

  @Test
  void initPayment_mobileCommand_succeedsWithNullWebhookBaseUrl(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    DatatransWebhookProperties webhookProperties = new DatatransWebhookProperties();
    // callbackBaseUrl is null by default
    DatatransReconciliationProperties reconciliationProperties =
        new DatatransReconciliationProperties();
    reconciliationProperties.setInitialDelay(Duration.ofMinutes(2));
    reconciliationProperties.setPollInterval(Duration.ofSeconds(30));
    reconciliationProperties.setMaxDuration(Duration.ofMinutes(30));

    var underTest = new TemporalWorkflowAdapter(
        client, webhookProperties, reconciliationProperties,
        new PaymentWorkflowProperties(), worker.getTaskQueue());

    var command = new NewCardMobileInitCommand(
        BASKET_ID, "gb", "en", "LEISURE", "APPS_ANDROID");

    PaymentInitResult result = underTest.initPayment(command);

    // Initialization should still succeed even without a webhook base URL
    assertThat(result.success()).isTrue();
    assertThat(result.transactionId()).isEqualTo("mobile-txn-stub");
  }

  // --- Error handling tests ---

  @Test
  void initPayment_returnsTypedErrorWhenPaymentMethodNotAvailable(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    activities.setValidationResult(new PaymentMethodValidationResult(false, List.of()));
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    var underTest = createAdapter(client, worker.getTaskQueue());

    var command = new NewCardWebInitCommand(
        BASKET_ID, RETURN_URL, "gb", "en", "LEISURE", "PI");

    PaymentInitResult result = underTest.initPayment(command);

    assertThat(result.success()).isFalse();
    assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE);
    assertThat(result.transactionId()).isNull();
  }

  @Test
  void initPayment_mapsFullyQualifiedApplicationFailureTypeToTypedErrorCode(
      TestWorkflowEnvironment testEnv, Worker worker, WorkflowClient client) {
    var activities = new PaymentActivitiesStub();
    activities.setReservationFailure(new BasketNotFoundException("No basket " + BASKET_ID));
    worker.registerActivitiesImplementations(activities);
    testEnv.start();

    var underTest = createAdapter(client, worker.getTaskQueue());

    var command = new NewCardWebInitCommand(
        BASKET_ID, RETURN_URL, "gb", "en", "LEISURE", "PI");

    PaymentInitResult result = underTest.initPayment(command);

    // Temporal reports ApplicationFailure.getType() as the exception's FQCN
    assertThat(result.success()).isFalse();
    assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.BASKET_NOT_FOUND);
  }

  // --- Helper methods ---

  private static TemporalWorkflowAdapter createAdapter(WorkflowClient client, String taskQueue) {
    DatatransWebhookProperties webhookProperties = new DatatransWebhookProperties();
    DatatransReconciliationProperties reconciliationProperties =
        new DatatransReconciliationProperties();
    reconciliationProperties.setInitialDelay(Duration.ofMinutes(2));
    reconciliationProperties.setPollInterval(Duration.ofSeconds(30));
    reconciliationProperties.setMaxDuration(Duration.ofMinutes(30));

    return new TemporalWorkflowAdapter(
        client, webhookProperties, reconciliationProperties,
        new PaymentWorkflowProperties(), taskQueue);
  }

  // --- Stub implementation ---

  static class PaymentActivitiesStub implements PaymentActivities {

    private final AtomicInteger datatransCallCount = new AtomicInteger();
    private final AtomicInteger mobileSdkCallCount = new AtomicInteger();
    private final AtomicReference<DatatransMobileSdkRequest> capturedMobileSdkRequest =
        new AtomicReference<>();
    private PaymentMethodValidationResult validationResult =
        new PaymentMethodValidationResult(true, List.of("VIS"));
    private RuntimeException reservationFailure;

    void setValidationResult(PaymentMethodValidationResult result) {
      this.validationResult = result;
    }

    void setReservationFailure(RuntimeException failure) {
      this.reservationFailure = failure;
    }


    @Override
    public DatatransAuthorizeResponse authorizeTransaction(String transactionId, String refno,
        long amount, String merchantId) {
      return null;
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
      if (reservationFailure != null) {
        throw reservationFailure;
      }
      return new Reservation(
          basketId, "HOTEL-001", new BigDecimal("86.00"),
          "GBP", "PI-123456789", "PI-123456789", "PI");
    }

    @Override
    public PaymentMethodValidationResult validatePaymentMethods(
        String basketReference, String hotelId, String country, String language,
        String userType, String clientChannel) {
      return validationResult;
    }

    @Override
    public String initDatatransSecureFields(DatatransSecureFieldsRequest request) {
      datatransCallCount.incrementAndGet();
      return "txn-update-with-start";
    }

    @Override
    public String initMobileSdkTransaction(DatatransMobileSdkRequest request) {
      mobileSdkCallCount.incrementAndGet();
      capturedMobileSdkRequest.set(request);
      return "mobile-txn-stub";
    }

    @Override
    public DatatransTransactionStatus getTransactionStatus(String transactionId,
        String merchantId) {
      return null;
    }


    @Override
    public void settleTransaction(
        String transactionId, long amount, String currency, String refno, String merchantId) {
    }


    @Override
    public void publishAuthorisedPaymentEvent(String basketId, String transactionId,
        String cardAlias, long authorizedAmount, String currency, String paymentMethod,
        String last4Digits, String expiry, PaymentOption paymentOption,
        String language) {
    }

    int getDatatransCallCount() {
      return datatransCallCount.get();
    }

    int getMobileSdkCallCount() {
      return mobileSdkCallCount.get();
    }

    DatatransMobileSdkRequest getCapturedMobileSdkRequest() {
      return capturedMobileSdkRequest.get();
    }
  }
}
