package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import io.temporal.api.enums.v1.WorkflowIdConflictPolicy;
import io.temporal.client.UpdateOptions;
import io.temporal.client.WithStartWorkflowOperation;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowNotFoundException;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.client.WorkflowUpdateHandle;
import io.temporal.client.WorkflowUpdateStage;
import io.temporal.client.WorkflowUpdateTimeoutOrCancelledException;
import io.temporal.failure.ApplicationFailure;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.MobileSdkReconciliationSettings;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardMobileInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentWorkflowPort;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentFailureMapper;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentWorkflow;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentWorkflowTuning;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransReconciliationProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransWebhookProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentWorkflowProperties;

/**
 * Unified infrastructure adapter implementing {@link PaymentWorkflowPort} via the Temporal SDK.
 *
 * <p>Replaces the previous {@code TemporalWorkflowAdapter} and {@code MobileSdkWorkflowAdapter}
 * with a single implementation that works with the unified {@link PaymentWorkflow} interface.
 *
 * <h2>Initialization</h2>
 * <p>Uses Temporal's Update-With-Start operation to atomically start the long-lived workflow
 * and execute the {@code init} update, returning a {@link PaymentInitResult} synchronously.
 *
 * <h2>Authorization</h2>
 * <p>Starts the workflow's {@code authorize} {@code @UpdateMethod} and waits a bounded time
 * ({@code integrations.payment.workflow.authorize-wait}) for its result. The update is durably
 * accepted before the wait begins, so a wait that elapses means "still running", not "failed" —
 * the caller gets {@code AUTHORIZATION_PENDING} and polls
 * {@link #getPaymentStatus(String)} for the outcome.
 *
 * <h2>Webhook signals</h2>
 * <p>Delivers Datatrans webhook payloads as signals to the running workflow.
 *
 * <p>The workflow ID is deterministic ({@code "payment-{basketId}"}) — one workflow per basket.
 *
 * <p>Excluded from the {@code integration} profile where no Temporal server is available.
 */
@Slf4j
@Component
@Profile("!integration")
public class TemporalWorkflowAdapter implements PaymentWorkflowPort {

  private static final String WORKFLOW_ID_PREFIX = "payment-";
  private static final String WEBHOOK_PATH = "/datatrans";

  /**
   * Update name routed to {@link PaymentWorkflow#authorize()}.
   *
   * <p>Its {@code @UpdateMethod} declares no explicit name, so Temporal registers the handler
   * under the bare method name. An untyped stub has no interface to derive that from, so the
   * string is spelled out here — get it wrong and the update is rejected as unregistered.
   */
  private static final String AUTHORIZE_UPDATE_NAME = "authorize";

  private final WorkflowClient workflowClient;
  private final DatatransWebhookProperties webhookProperties;
  private final DatatransReconciliationProperties reconciliationProperties;
  private final PaymentWorkflowProperties workflowProperties;
  private final String taskQueue;

  public TemporalWorkflowAdapter(
      WorkflowClient workflowClient,
      DatatransWebhookProperties webhookProperties,
      DatatransReconciliationProperties reconciliationProperties,
      PaymentWorkflowProperties workflowProperties,
      @Value("${temporal.task-queue:payment-workflows}") String taskQueue) {
    this.workflowClient = workflowClient;
    this.webhookProperties = webhookProperties;
    this.reconciliationProperties = reconciliationProperties;
    this.workflowProperties = workflowProperties;
    this.taskQueue = taskQueue;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Uses Update-With-Start to atomically start the {@link PaymentWorkflow} (or reuse an
   * existing one for the same basket) and call the {@code init} update. For mobile commands,
   * enriches the command with the webhook URL and reconciliation settings before sending.
   */
  @Override
  public PaymentInitResult initPayment(PaymentInitCommand command) {
    String workflowId = WORKFLOW_ID_PREFIX + command.basketId();
    log.info("Initiating payment via unified workflow [workflowId={}, method={}]",
        workflowId, command.paymentMethod());

    try {
      PaymentInitCommand enrichedCommand = enrichCommandIfMobile(command);

      WorkflowOptions options = WorkflowOptions.newBuilder()
          .setWorkflowId(workflowId)
          .setTaskQueue(taskQueue)
          .setWorkflowIdConflictPolicy(
              WorkflowIdConflictPolicy.WORKFLOW_ID_CONFLICT_POLICY_USE_EXISTING)
          // Workflow timings travel as start memos: the workflow cannot read Spring
          // configuration, and a memo stays in the history so replays stay deterministic.
          .setMemo(workflowTuningMemo())
          .build();

      PaymentWorkflow workflow = workflowClient.newWorkflowStub(
          PaymentWorkflow.class, options);

      PaymentInitResult result = WorkflowClient.executeUpdateWithStart(
          workflow::init,
          enrichedCommand,
          UpdateOptions.<PaymentInitResult>newBuilder().build(),
          new WithStartWorkflowOperation<>(workflow::run, command.basketId()));

      log.info("Payment workflow initialized [workflowId={}, success={}, transactionId={}]",
          workflowId, result.success(),
          result.success() ? result.transactionId() : "N/A");
      return result;
    } catch (WorkflowException e) {
      return mapWorkflowInitError(e, workflowId);
    } catch (Exception e) {
      log.error("Failed to interact with Temporal workflow [workflowId={}]", workflowId, e);
      // The exception text stays in the log only: a gRPC or SDK message can carry hostnames and
      // stack detail that an API caller has no use for and should not see.
      return new PaymentInitResult(false, null, PaymentErrorCode.GATEWAY_ERROR,
          "Failed to communicate with payment workflow");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Starts the {@code authorize} update and waits a bounded time for its result, rather than
   * blocking on the typed call for as long as the handler takes. The handler can legitimately run
   * for minutes (activity retries, the authorised-event publish horizon, compensation) — far past
   * the point where a load balancer or a browser has already given up on the request.
   *
   * <p>The two steps matter in that order. {@code startUpdate} returns only once the update is
   * durably admitted to the workflow's history, so the authorization is committed before the
   * clock starts; the subsequent wait is purely about how long this thread is willing to hold the
   * caller's connection. When it elapses the workflow carries on unaffected and the caller is
   * told {@code AUTHORIZATION_PENDING} — never a fabricated gateway error for a payment that may
   * yet succeed.
   */
  @Override
  public AuthorizeResult authorizePayment(String basketId) {
    String workflowId = WORKFLOW_ID_PREFIX + basketId;
    Duration wait = workflowProperties.getAuthorizeWait();
    log.info("Authorizing payment via unified workflow update [workflowId={}, wait={}]",
        workflowId, wait);

    try {
      WorkflowStub untypedStub = workflowClient.newUntypedWorkflowStub(workflowId);

      // Durably admit the update first; only then start counting against the caller's patience.
      WorkflowUpdateHandle<AuthorizeResult> handle = untypedStub.startUpdate(
          UpdateOptions.newBuilder(AuthorizeResult.class)
              .setUpdateName(AUTHORIZE_UPDATE_NAME)
              .setWaitForStage(WorkflowUpdateStage.ACCEPTED)
              .build());

      AuthorizeResult result = handle.getResult(wait.toMillis(), TimeUnit.MILLISECONDS);

      log.info("Authorization result received [workflowId={}, success={}]",
          workflowId, result.success());
      return result;
    } catch (WorkflowUpdateTimeoutOrCancelledException e) {
      log.info("Authorization still running after the bounded wait "
              + "[workflowId={}, wait={}]; the workflow continues and the caller was told to poll",
          workflowId, wait);
      return new AuthorizeResult(false, PaymentErrorCode.AUTHORIZATION_PENDING,
          PaymentErrorCode.AUTHORIZATION_PENDING.getErrorMessage());
    } catch (WorkflowNotFoundException e) {
      log.warn("No workflow found for authorization [workflowId={}]", workflowId);
      return new AuthorizeResult(false, PaymentErrorCode.TRANSACTION_NOT_FOUND,
          "No payment workflow found for basket " + basketId);
    } catch (Exception e) {
      log.error("Failed to authorize payment via workflow update [workflowId={}]",
          workflowId, e);
      // Constant message: the exception text belongs in the log, not the API response.
      return new AuthorizeResult(false, PaymentErrorCode.GATEWAY_ERROR,
          "Failed to communicate with payment workflow");
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Served by the workflow's {@code getPaymentStatus} and {@code getAuthorizeResult} queries,
   * so polling costs the workflow nothing and cannot change its state.
   */
  @Override
  public PaymentStatusResponse getPaymentStatus(String basketId) {
    String workflowId = WORKFLOW_ID_PREFIX + basketId;

    try {
      PaymentWorkflow workflowStub = workflowClient.newWorkflowStub(
          PaymentWorkflow.class, workflowId);

      return new PaymentStatusResponse(
          workflowStub.getPaymentStatus(), workflowStub.getAuthorizeResult());
    } catch (WorkflowNotFoundException e) {
      log.warn("No workflow found for status query [workflowId={}]", workflowId);
      throw new BasketNotFoundException(
          "No payment workflow found for basket " + basketId, e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * <p>Signals the running workflow with the Datatrans webhook payload. If no workflow
   * exists for the basket, the signal is dropped gracefully (logged warning) to ensure
   * the webhook endpoint still returns 200 OK to Datatrans.
   */
  @Override
  public void signalWebhookReceived(String basketId, WebhookPayload payload) {
    String workflowId = WORKFLOW_ID_PREFIX + basketId;

    try {
      PaymentWorkflow workflowStub = workflowClient.newWorkflowStub(
          PaymentWorkflow.class, workflowId);

      workflowStub.webhookReceived(payload);
      log.info("Datatrans webhook signal sent [workflowId={}, transactionId={}]",
          workflowId, payload == null ? null : payload.transactionId());
    } catch (WorkflowNotFoundException e) {
      log.warn("No open payment workflow to signal for webhook [workflowId={}]", workflowId);
    }
  }

  // --- Private helpers ---

  /**
   * Builds the start memo carrying every configured workflow timing.
   *
   * <p>Values are written as milliseconds because a memo is serialised into the history and a
   * primitive survives any future change to how durations are represented. Workflows started
   * before a key existed read {@code null} for it and fall back to the shipped default, so
   * adding a key here never breaks a running execution.
   */
  private Map<String, Object> workflowTuningMemo() {
    var settlement = workflowProperties.getSettlement();
    var bookingPoll = workflowProperties.getBookingPoll();
    return Map.of(
        PaymentWorkflowTuning.EXPIRY_TIMEOUT_MEMO_KEY,
        workflowProperties.getTimeout().toMillis(),
        PaymentWorkflowTuning.SETTLEMENT_HORIZON_MEMO_KEY,
        settlement.getRetryHorizon().toMillis(),
        PaymentWorkflowTuning.SETTLEMENT_BACKOFF_CAP_MEMO_KEY,
        settlement.getBackoffCap().toMillis(),
        PaymentWorkflowTuning.SETTLEMENT_ATTEMPT_TIMEOUT_MEMO_KEY,
        settlement.getAttemptTimeout().toMillis(),
        PaymentWorkflowTuning.PUBLISH_HORIZON_MEMO_KEY,
        workflowProperties.getPublish().getRetryHorizon().toMillis(),
        PaymentWorkflowTuning.BOOKING_POLL_INTERVAL_MEMO_KEY,
        bookingPoll.getInterval().toMillis(),
        PaymentWorkflowTuning.BOOKING_POLL_HORIZON_MEMO_KEY,
        bookingPoll.getHorizon().toMillis());
  }

  /**
   * Enriches mobile init commands with webhook URL and reconciliation settings.
   *
   * <p>For {@link NewCardMobileInitCommand}, a new command instance is created carrying the
   * webhook callback URL (derived from configuration) and reconciliation timing settings.
   * Web commands pass through unchanged.
   */
  private PaymentInitCommand enrichCommandIfMobile(PaymentInitCommand command) {
    if (command instanceof NewCardMobileInitCommand mobileCmd) {
      String webhookUrl = buildWebhookUrl(mobileCmd.basketId());
      MobileSdkReconciliationSettings reconciliation = toReconciliationSettings(
          reconciliationProperties);

      return new NewCardMobileInitCommand(
          mobileCmd.basketId(),
          mobileCmd.country(),
          mobileCmd.language(),
          mobileCmd.userType(),
          mobileCmd.clientChannel(),
          webhookUrl,
          reconciliation);
    }
    return command;
  }

  /**
   * Builds the public Datatrans callback URL carrying the {@code basketId} correlation key.
   *
   * <p>Assembled from the configured callback base URL plus {@code /datatrans?basketId=...}.
   * The webhook endpoint is {@code /api/payments/webhooks/datatrans}, so the base URL is
   * expected to end with {@code /api/payments/webhooks}. Returns {@code null} when no base URL
   * is configured, in which case no {@code webhook.url} is sent to Datatrans.
   */
  private String buildWebhookUrl(String basketId) {
    String baseUrl = webhookProperties.getCallbackBaseUrl();
    if (baseUrl == null || baseUrl.isBlank()) {
      log.warn("No Datatrans webhook callback base URL configured; "
          + "init will not register a webhook URL");
      return null;
    }

    String trimmedBaseUrl = baseUrl.endsWith("/")
        ? baseUrl.substring(0, baseUrl.length() - 1)
        : baseUrl;

    return trimmedBaseUrl + WEBHOOK_PATH + "?basketId="
        + URLEncoder.encode(basketId == null ? "" : basketId, StandardCharsets.UTF_8);
  }

  /**
   * Converts externalised {@link DatatransReconciliationProperties} to a workflow-safe
   * settings record that can be serialized into the Temporal workflow history.
   */
  static MobileSdkReconciliationSettings toReconciliationSettings(
      DatatransReconciliationProperties properties) {
    return new MobileSdkReconciliationSettings(
        properties.isEnabled(),
        properties.getInitialDelay().toMillis(),
        properties.getPollInterval().toMillis(),
        properties.getMaxDuration().toMillis());
  }

  /**
   * Maps a Temporal {@link WorkflowException} to a {@link PaymentInitResult} error.
   *
   * <p>Extracts the {@link ApplicationFailure} from the exception cause chain to obtain
   * the typed error code and message.
   */
  private PaymentInitResult mapWorkflowInitError(WorkflowException e, String workflowId) {
    Throwable cause = e.getCause();
    while (cause != null && !(cause instanceof ApplicationFailure)) {
      cause = cause.getCause();
    }

    PaymentErrorCode errorCode;
    String errorMessage;

    if (cause instanceof ApplicationFailure applicationFailure) {
      errorCode = mapFailureTypeToErrorCode(applicationFailure.getType());
      errorMessage = applicationFailure.getOriginalMessage();
      log.warn("Payment workflow failed with application error "
              + "[workflowId={}, errorCode={}, message={}]",
          workflowId, errorCode, errorMessage);
    } else {
      errorCode = PaymentErrorCode.GATEWAY_ERROR;
      // Unlike the ApplicationFailure branch above, whose message is a domain error written for
      // callers, this exception text is SDK internals — log it, don't return it.
      errorMessage = "Workflow execution failed";
      log.error("Payment workflow failed with unexpected error [workflowId={}]",
          workflowId, e);
    }

    return new PaymentInitResult(false, null, errorCode, errorMessage);
  }

  /**
   * Maps an {@link ApplicationFailure} type string to the corresponding
   * {@link PaymentErrorCode}.
   *
   * <p>Temporal sets {@code ApplicationFailure.getType()} to the fully qualified class name of
   * the original domain exception, so the mapping keys are FQCN constants derived from the
   * exception classes themselves rather than hand-written strings.
   */
  private PaymentErrorCode mapFailureTypeToErrorCode(String failureType) {
    return PaymentFailureMapper.fromFailureType(failureType);
  }
}
