package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.logic.AmountCalculator;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransCardInfo;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransMobileSdkRequest;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.MobileSdkReconciliationSettings;
import uk.co.whitbread.payment.orchestrator.domain.model.NewCardMobileInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentMethodValidationResult;
import uk.co.whitbread.payment.orchestrator.domain.model.Reservation;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

/**
 * Strategy for Mobile SDK (native) payment initialization and authorization.
 *
 * <p>Handles the {@link PaymentMethod#NEW_CARD_MOBILE} integration type. After initialization,
 * authorization is driven by either an asynchronous Datatrans webhook or, when reconciliation
 * is enabled, a polling loop that calls {@code getTransactionStatus} at configured intervals.
 * The webhook can arrive at any time and wins over polling — both paths converge on the same
 * authorization-outcome logic.
 *
 * <p>This strategy is a plain deterministic Java class with no Spring dependencies. It runs
 * within the Temporal workflow context, so {@code Workflow.*} calls are safe.
 *
 * @see PaymentMethodStrategy
 * @see WorkflowState
 */
public class NewCardMobileStrategy implements PaymentMethodStrategy {

  private static final Logger log = Workflow.getLogger(NewCardMobileStrategy.class);

  // --- Datatrans status constants ---
  private static final String STATUS_AUTHORIZED = "authorized";
  private static final String STATUS_SETTLED = "settled";
  private static final String STATUS_CANCELED = "canceled";
  private static final String STATUS_FAILED = "failed";
  private static final List<String> IN_FLIGHT_STATUSES =
      List.of("initialized", "pending", "processing");

  private final AmountCalculator amountCalculator = new AmountCalculator();

  @Override
  public PaymentInitResult init(PaymentInitCommand command, PaymentActivities activities,
      WorkflowState state) {
    if (!(command instanceof NewCardMobileInitCommand mobileCommand)) {
      return new PaymentInitResult(false, null,
          PaymentErrorCode.VALIDATION_FAILED,
          "Expected NewCardMobileInitCommand but received " + command.getClass().getSimpleName());
    }

    try {
      // Step 1: Get reservation from Hotel Reservation Entity Service
      Reservation reservation = activities.getReservation(mobileCommand.basketId());

      // Step 2: Validate payment methods and get available card brands
      // Its own stub: a 4xx from the Payment Method Entity Service is an answer, not an
      // outage, and must not be retried. See PaymentActivityStubs#paymentMethodActivities.
      PaymentMethodValidationResult paymentMethodResult =
          PaymentActivityStubs.paymentMethodActivities().validatePaymentMethods(
              mobileCommand.basketId(),
              reservation.hotelId(),
              mobileCommand.country(),
              mobileCommand.language(),
              mobileCommand.userType(),
              mobileCommand.clientChannel());

      if (!paymentMethodResult.cardPaymentAvailable()) {
        return new PaymentInitResult(false, null,
            PaymentErrorCode.PAYMENT_METHOD_NOT_AVAILABLE,
            "Card payment is not available for this hotel");
      }

      List<String> paymentMethods = paymentMethodResult.availableCardBrands();

      // Step 3: Calculate amount in minor units
      long amountInMinorUnits = amountCalculator.toMinorUnits(
          reservation.totalCostOfStay(), reservation.currencyCode());

      // Step 4: Store payment state needed to correlate and process the webhook
      state.setAmount(amountInMinorUnits);
      state.setCurrency(reservation.currencyCode());
      state.setBasketId(mobileCommand.basketId());
      state.setBookingReference(reservation.bookingReference());
      state.setLanguage(mobileCommand.language());
      state.setReconciliationSettings(mobileCommand.reconciliation());

      // Step 5: Resolve merchant ID for the hotel
      String merchantId = activities.resolveMerchantId(reservation.hotelId());
      state.setMerchantId(merchantId);

      // Step 6: Build Datatrans v2 Mobile SDK request and initialize the transaction.
      // The webhook URL is assembled by the adapter, which enriches the command before it
      // reaches the workflow. The DatatransMobileSdkRequest accepts a null webhook URL.
      var request = new DatatransMobileSdkRequest(
          amountInMinorUnits,
          reservation.currencyCode(),
          reservation.refno(),
          paymentMethods,
          merchantId,
          mobileCommand.webhookUrl()
      );

      String transactionId = activities.initMobileSdkTransaction(request);
      if (transactionId == null || transactionId.isBlank()) {
        // No FAILED here: like every other init failure, the error is the result and the
        // workflow stays alive (INITIALIZED, no transactionId) for another attempt.
        return new PaymentInitResult(false, null,
            PaymentErrorCode.GATEWAY_ERROR,
            "Datatrans did not return a transaction identifier");
      }

      // Step 7: Store transaction ID and reconciliation timing
      state.setTransactionId(transactionId);
      state.setInitCompletedAtMillis(Workflow.currentTimeMillis());

      // Step 8: Transition basket to PAY_PENDING before returning txnId to mobile app
      activities.changeBasketStatus(reservation.bookingReference(), "PAY_PENDING");

      return new PaymentInitResult(true, transactionId, null, null);

    } catch (Exception e) {
      // Report the failure as a result rather than throwing it: a thrown update surfaces
      // through a second error channel (the adapter's failure mapping) and diverges from the
      // web strategy's contract. The state is left as PaymentWorkflowImpl#init reset it
      // (INITIALIZED, no transactionId), which is exactly what the web strategy leaves behind:
      // the workflow stays alive for another attempt, and the expiry deadline ends it if none
      // comes.
      PaymentErrorCode errorCode = PaymentFailureMapper.toErrorCode(e);
      log.warn("Mobile SDK initialization failed [basketId={}, errorCode={}]",
          mobileCommand.basketId(), errorCode, e);
      return new PaymentInitResult(false, null, errorCode, errorCode.getErrorMessage());
    }
  }

  @Override
  public void awaitAuthorization(PaymentActivities activities, WorkflowState state) {
    MobileSdkReconciliationSettings reconciliation = state.getReconciliationSettings();

    // If reconciliation is disabled or not configured, fall back to webhook-only await.
    if (reconciliation == null || !reconciliation.enabled()) {
      awaitWebhookOnly(activities, state);
      return;
    }

    // Full reconciliation polling loop (CTECH-12128 pattern).
    reconciliationLoop(activities, state, reconciliation);
  }

  @Override
  public PaymentMethod getSupportedMethod() {
    return PaymentMethod.NEW_CARD_MOBILE;
  }

  // -----------------------------------------------------------------------
  // Webhook-only await (reconciliation disabled)
  // -----------------------------------------------------------------------

  /**
   * Waits for webhook payloads to arrive in the event-inbox and processes each one until the
   * gateway confirms an outcome.
   *
   * <p>A loop rather than a single await: a webhook whose claimed outcome the gateway does not
   * confirm (still in-flight there) is dropped, and the strategy goes back to waiting for the
   * next delivery rather than resolving the payment on an unconfirmed claim.
   */
  private void awaitWebhookOnly(PaymentActivities activities, WorkflowState state) {
    while (true) {
      Workflow.await(() -> state.getWebhookPayload() != null
          || state.getPaymentStatus().authorizationPhaseComplete());

      // Something else already produced the authorization outcome (the reconciliation poll, or
      // an expiry) — there is no webhook left to act on.
      if (state.getPaymentStatus().authorizationPhaseComplete()) {
        return;
      }

      if (processWebhookPayload(activities, state)) {
        return;
      }
    }
  }

  // -----------------------------------------------------------------------
  // Full reconciliation loop (CTECH-12128)
  // -----------------------------------------------------------------------

  /**
   * Implements the complete reconciliation polling loop. The webhook can arrive at any time
   * and wins over polling — both paths converge on the same authorize logic.
   */
  private void reconciliationLoop(PaymentActivities activities, WorkflowState state,
      MobileSdkReconciliationSettings settings) {

    long initCompletedAt = state.getInitCompletedAtMillis();
    long deadline = initCompletedAt + settings.maxDurationMillis();
    long nextPollAt = initCompletedAt + settings.initialDelayMillis();

    PaymentActivities statusActivities = createStatusActivities();

    while (state.getPaymentStatus() == PaymentStatus.INITIALIZED) {
      long now = Workflow.currentTimeMillis();

      // Check deadline expiry
      if (now >= deadline) {
        expireIfStillInitialized(state);
        return;
      }

      // Wait until next poll time, but wake early if webhook arrives or terminal state reached
      long waitMillis = Math.max(0, nextPollAt - now);
      if (waitMillis > 0) {
        Workflow.await(Duration.ofMillis(waitMillis), () -> state.getWebhookPayload() != null
            || state.getPaymentStatus().authorizationPhaseComplete());
      }

      // A webhook may have arrived or state changed during the wait
      if (state.getPaymentStatus().authorizationPhaseComplete()) {
        return;
      }

      // Process webhook if it arrived (webhook wins over polling). An unconfirmed webhook —
      // one whose claim the gateway does not back yet — is dropped and the poll loop resumes.
      if (state.getWebhookPayload() != null) {
        if (processWebhookPayload(activities, state)) {
          return;
        }
        nextPollAt = Workflow.currentTimeMillis() + settings.pollIntervalMillis();
        continue;
      }

      // Re-check terminal state after potential webhook processing
      if (state.getPaymentStatus() != PaymentStatus.INITIALIZED) {
        return;
      }

      // Re-check deadline after wait
      now = Workflow.currentTimeMillis();
      if (now >= deadline) {
        expireIfStillInitialized(state);
        return;
      }

      // Defensive invariant: never call Datatrans without a transaction identifier
      String pollTransactionId = state.getTransactionId();
      if (pollTransactionId == null || pollTransactionId.isBlank()) {
        log.warn("Reconciliation loop has no transactionId [basketId={}]",
            state.getBasketId());
        awaitWebhookOnly(activities, state);
        return;
      }

      // Execute status poll
      state.setPollAttemptCount(state.getPollAttemptCount() + 1);
      log.info("Mobile SDK reconciliation poll [transactionId={}, attempt={}]",
          pollTransactionId, state.getPollAttemptCount());

      DatatransTransactionStatus result;
      try {
        result = statusActivities.getTransactionStatus(
            pollTransactionId, state.getMerchantId());
      } catch (RuntimeException e) {
        // Check if state changed during the activity execution (webhook won the race)
        if (state.getPaymentStatus() != PaymentStatus.INITIALIZED) {
          log.info("Discarding stale reconciliation failure [transactionId={}]",
              pollTransactionId);
          return;
        }

        if (PaymentFailureMapper.hasFailureType(e, TransactionNotFoundException.class)) {
          log.warn("Reconciliation transaction not found [transactionId={}]",
              pollTransactionId);
          state.setPaymentStatus(PaymentStatus.FAILED);
          state.setAttemptFailed(true);
          state.setAuthorizeResult(new AuthorizeResult(false,
              PaymentErrorCode.GATEWAY_ERROR, "Transaction not found"));
          return;
        }

        // Transient failures are contained to this poll — schedule next attempt
        log.warn("Reconciliation poll failed [transactionId={}, type={}]",
            pollTransactionId, PaymentFailureMapper.extractFailureType(e));
        nextPollAt = Workflow.currentTimeMillis() + settings.pollIntervalMillis();
        continue;
      }

      // The status activity yielded to Temporal — a webhook can win while it was in flight
      if (state.getPaymentStatus() != PaymentStatus.INITIALIZED) {
        log.info("Discarding stale reconciliation result [transactionId={}]",
            pollTransactionId);
        return;
      }

      // Also check if webhook arrived during status poll
      if (state.getWebhookPayload() != null) {
        if (processWebhookPayload(activities, state)) {
          return;
        }
        nextPollAt = Workflow.currentTimeMillis() + settings.pollIntervalMillis();
        continue;
      }

      // Re-check deadline after activity returned
      if (Workflow.currentTimeMillis() >= deadline) {
        expireIfStillInitialized(state);
        return;
      }

      // Validate result matches the expected transaction
      if (result == null || !pollTransactionId.equals(result.transactionId())) {
        log.warn("Reconciliation transaction mismatch [expected={}, received={}]",
            pollTransactionId, result == null ? null : result.transactionId());
        nextPollAt = Workflow.currentTimeMillis() + settings.pollIntervalMillis();
        continue;
      }

      // Process the polling result
      String normalizedStatus = normaliseStatus(result.status());
      switch (normalizedStatus) {
        case STATUS_AUTHORIZED, STATUS_SETTLED -> {
          doAuthorize(activities, state, result);
          log.info("Reconciliation authorized [status={}, paymentStatus={}]",
              normalizedStatus, state.getPaymentStatus());
          return;
        }
        case STATUS_CANCELED -> {
          state.setPaymentStatus(PaymentStatus.CANCELLED);
          state.setAttemptFailed(true);
          state.setAuthorizeResult(new AuthorizeResult(false,
              PaymentErrorCode.INVALID_TRANSACTION_STATE, "Payment was cancelled"));
          log.info("Reconciliation cancelled [transactionId={}]", pollTransactionId);
          return;
        }
        case STATUS_FAILED -> {
          state.setPaymentStatus(PaymentStatus.FAILED);
          state.setAttemptFailed(true);
          state.setAuthorizeResult(new AuthorizeResult(false,
              PaymentErrorCode.GATEWAY_ERROR, "Payment failed"));
          log.info("Reconciliation failed [transactionId={}]", pollTransactionId);
          return;
        }
        default -> {
          if (IN_FLIGHT_STATUSES.contains(normalizedStatus)) {
            log.info("Reconciliation in-flight [status={}]", normalizedStatus);
          } else {
            log.warn("Reconciliation returned unknown status [transactionId={}, status={}]",
                pollTransactionId, normalizedStatus);
          }
          nextPollAt = Workflow.currentTimeMillis() + settings.pollIntervalMillis();
        }
      }
    }
  }

  // -----------------------------------------------------------------------
  // Webhook processing with status validation
  // -----------------------------------------------------------------------

  /**
   * Processes a webhook payload from the event-inbox as a hint, never as a fact.
   *
   * <p>The webhook endpoint may be reachable without a verified signature, so the payload's
   * claimed status — positive or negative — is only ever a trigger to ask Datatrans what
   * actually happened (the CTECH-11615 pattern, extended to every status). Failing or
   * cancelling on an unverified claim would let one forged POST kill a live payment attempt;
   * the gateway's own answer is the only thing that resolves the payment.
   *
   * @return {@code true} if the payment reached an outcome; {@code false} if the webhook's
   *     claim was not confirmed by the gateway (still in-flight there) and was dropped — the
   *     caller should resume waiting or polling
   */
  private boolean processWebhookPayload(PaymentActivities activities, WorkflowState state) {
    WebhookPayload rawPayload = state.getWebhookPayload();
    if (rawPayload == null) {
      return false;
    }

    if (!(rawPayload instanceof DatatransWebhookPayload payload)) {
      log.warn("Unexpected webhook payload type [type={}]",
          rawPayload.getClass().getSimpleName());
      state.setPaymentStatus(PaymentStatus.FAILED);
      state.setAttemptFailed(true);
      state.setAuthorizeResult(new AuthorizeResult(false,
          PaymentErrorCode.GATEWAY_ERROR, "Unexpected webhook payload type"));
      return true;
    }

    log.info("Webhook received, confirming with gateway [transactionId={}, claimedStatus={}]",
        state.getTransactionId(), normaliseStatus(payload.status()));
    return resolveOutcomeFromGateway(activities, state);
  }

  /**
   * Resolves the payment outcome from the gateway's own transaction status (CTECH-11615).
   *
   * <p>Sets {@code authorizationInProgress} to prevent the reconciliation poller from racing,
   * calls {@code getTransactionStatus}, and lets the confirmed status — never the webhook's
   * claim — decide the outcome: AUTHORIZED only for {@code authorized}/{@code settled}, FAILED
   * or CANCELLED only when the gateway itself says so. Card data for the downstream event is
   * sourced from the status response (backend-confirmed), not the webhook payload.
   *
   * <p>A gateway status that is still in-flight means the webhook's claim is not (or not yet)
   * true. The webhook is dropped from the inbox and the payment stays INITIALIZED: resolving on
   * the unconfirmed claim is exactly what an unauthenticated forged webhook would exploit, and
   * a genuine race just resolves on a later webhook or reconciliation poll.
   *
   * @return {@code true} if the payment reached an outcome (or something else already resolved
   *     it); {@code false} if the gateway reported the transaction still in-flight and the
   *     webhook was dropped
   */
  private boolean resolveOutcomeFromGateway(PaymentActivities activities, WorkflowState state) {
    state.setAuthorizationInProgress(true);
    try {
      PaymentActivities statusActivities = createStatusActivities();
      DatatransTransactionStatus statusResponse =
          statusActivities.getTransactionStatus(
              state.getTransactionId(), state.getMerchantId());

      // The status activity yielded to Temporal. A concurrent signal could have transitioned
      // the workflow while it was in flight — discard if no longer INITIALIZED.
      if (state.getPaymentStatus() != PaymentStatus.INITIALIZED) {
        return true;
      }

      String confirmedStatus = normaliseStatus(
          statusResponse == null ? null : statusResponse.status());
      log.info("Webhook status validation [transactionId={}, confirmedStatus={}]",
          state.getTransactionId(), confirmedStatus);

      switch (confirmedStatus) {
        case STATUS_AUTHORIZED, STATUS_SETTLED ->
            doAuthorize(activities, state, statusResponse);
        case STATUS_FAILED -> {
          state.setPaymentStatus(PaymentStatus.FAILED);
          state.setAttemptFailed(true);
          state.setAuthorizeResult(new AuthorizeResult(false,
              PaymentErrorCode.GATEWAY_ERROR, "Payment failed on status validation"));
        }
        case STATUS_CANCELED -> {
          state.setPaymentStatus(PaymentStatus.CANCELLED);
          state.setAttemptFailed(true);
          state.setAuthorizeResult(new AuthorizeResult(false,
              PaymentErrorCode.INVALID_TRANSACTION_STATE, "Payment was cancelled"));
        }
        default -> {
          if (IN_FLIGHT_STATUSES.contains(confirmedStatus)) {
            log.info("Webhook claim not confirmed — gateway still in-flight; dropping webhook "
                    + "[transactionId={}, confirmedStatus={}]",
                state.getTransactionId(), confirmedStatus);
            state.setWebhookPayload(null);
            return false;
          }
          log.warn("Webhook status validation returned unexpected status "
                  + "[transactionId={}, confirmedStatus={}]",
              state.getTransactionId(), confirmedStatus);
          state.setPaymentStatus(PaymentStatus.FAILED);
          state.setAttemptFailed(true);
          state.setAuthorizeResult(new AuthorizeResult(false,
              PaymentErrorCode.GATEWAY_ERROR,
              "Unexpected status during validation: " + confirmedStatus));
        }
      }
      return true;
    } catch (RuntimeException e) {
      // The status activity yielded — check for concurrent transition
      if (state.getPaymentStatus() != PaymentStatus.INITIALIZED) {
        return true;
      }

      if (PaymentFailureMapper.hasFailureType(e, TransactionNotFoundException.class)) {
        log.warn("Webhook status validation transaction not found [transactionId={}]",
            state.getTransactionId());
      } else {
        log.warn("Webhook status validation failed [transactionId={}, error={}]",
            state.getTransactionId(), e.getMessage());
      }

      state.setPaymentStatus(PaymentStatus.FAILED);
      state.setAttemptFailed(true);
      state.setAuthorizeResult(new AuthorizeResult(false,
          PaymentErrorCode.GATEWAY_ERROR, "Status validation failed"));
      return true;
    } finally {
      state.setAuthorizationInProgress(false);
    }
  }

  // -----------------------------------------------------------------------
  // Authorization outcome
  // -----------------------------------------------------------------------

  /**
   * Core authorization logic — publishes the {@code PaymentAuthorisedEvent} and transitions
   * to AUTHORIZED. Card data is stored for the downstream event only and is never logged.
   */
  private void doAuthorize(PaymentActivities activities, WorkflowState state,
      DatatransTransactionStatus statusResponse) {
    state.setAuthorizationInProgress(true);
    try {
      String cardAlias = statusResponse.card() == null
          ? null : statusResponse.card().alias();
      // The gateway confirmed the authorization; an omitted echo of the amount does not make
      // the authorized amount zero, so fall back to the amount we asked it to authorize.
      long authorizedAmount = statusResponse.authorizedAmount() == null
          ? state.getAmount() : statusResponse.authorizedAmount().longValue();
      String acquirerAuthCode = statusResponse.acquirerAuthorizationCode();
      String currency = statusResponse.currency();
      String paymentMethod = statusResponse.paymentMethod();
      String last4Digits = deriveLast4Digits(statusResponse.card());
      String expiry = formatExpiry(statusResponse.card());

      // Store card data on state for downstream use
      state.setCardAlias(cardAlias);
      state.setAuthorizedAmount(authorizedAmount);
      state.setAcquirerAuthorizationCode(acquirerAuthCode);
      if (currency != null && !currency.isBlank()) {
        state.setCurrency(currency);
      }

      // Publish PaymentAuthorisedEvent — settlement is deferred until BookingCompletedEvent.
      // The publisher compensates (cancel + FAILED) if the event cannot be delivered within its
      // horizon, because an authorization whose event never landed holds money against a booking
      // that will never be started.
      boolean published = AuthorisedEventPublisher.publish(activities, state,
          authorizedAmount, paymentMethod, last4Digits, expiry);
      if (!published) {
        return;
      }

      state.setPaymentStatus(PaymentStatus.AUTHORIZED);
      state.setAuthorizeResult(new AuthorizeResult(true, null, null));
    } catch (RuntimeException e) {
      log.error("Authorization event publishing failed [transactionId={}, error={}]",
          state.getTransactionId(), e.getMessage(), e);
      state.setPaymentStatus(PaymentStatus.FAILED);
      state.setAttemptFailed(true);
      state.setAuthorizeResult(new AuthorizeResult(false,
          PaymentErrorCode.GATEWAY_ERROR, "Authorization event publishing failed"));
    } finally {
      state.setAuthorizationInProgress(false);
    }
  }

  // -----------------------------------------------------------------------
  // Expiry
  // -----------------------------------------------------------------------

  private void expireIfStillInitialized(WorkflowState state) {
    if (state.getPaymentStatus() == PaymentStatus.INITIALIZED) {
      state.setPaymentStatus(PaymentStatus.EXPIRED);
      state.setAttemptFailed(true);
      state.setAuthorizeResult(new AuthorizeResult(false,
          PaymentErrorCode.EXPIRED, "Payment session has expired"));
      log.warn("Mobile SDK reconciliation expired [transactionId={}, attempts={}]",
          state.getTransactionId(), state.getPollAttemptCount());
    }
  }

  // -----------------------------------------------------------------------
  // Status activity stub (tighter timeouts for reconciliation polling)
  // -----------------------------------------------------------------------

  /**
   * Creates an activity stub with tighter timeouts suitable for reconciliation status
   * polling (5s start-to-close, 2 max attempts, 1s initial interval, no backoff,
   * do not retry TransactionNotFoundException or DatatransAuthenticationException).
   *
   * <p>Rejected merchant credentials are not retried for the same reason a missing transaction
   * is not: the second attempt sends the same password and gets the same answer.
   */
  private PaymentActivities createStatusActivities() {
    return Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(5))
            .setRetryOptions(RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setBackoffCoefficient(1.0)
                .setMaximumAttempts(2)
                .setDoNotRetry(
                    TransactionNotFoundException.class.getName(),
                    DatatransAuthenticationException.class.getName())
                .build())
            .build());
  }

  // -----------------------------------------------------------------------
  // Datatrans helpers
  // -----------------------------------------------------------------------

  private static String normaliseStatus(String status) {
    return status == null ? "" : status.trim().toLowerCase(Locale.ROOT);
  }

  /**
   * Derives the last 4 digits from the masked card number (e.g. "424242xxxxxx4242" → "4242").
   */
  private static String deriveLast4Digits(DatatransCardInfo card) {
    if (card == null || card.masked() == null || card.masked().length() < 4) {
      return null;
    }
    return card.masked().substring(card.masked().length() - 4);
  }

  /**
   * Formats expiry as "MM/YY" from the card info's expiryMonth and expiryYear fields.
   */
  private static String formatExpiry(DatatransCardInfo card) {
    if (card == null || card.expiryMonth() == null || card.expiryYear() == null) {
      return null;
    }
    return card.expiryMonth() + "/" + card.expiryYear();
  }
}
