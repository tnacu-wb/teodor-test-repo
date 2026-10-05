package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;
import io.temporal.workflow.WorkflowInit;
import java.time.Duration;
import org.slf4j.Logger;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.BasketStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransTransactionStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

/**
 * Unified Temporal workflow implementation for payment orchestration.
 *
 * <p>Replaces both {@code SecureFieldsPaymentWorkflowImpl} and
 * {@code MobileSdkPaymentWorkflowImpl} with a single workflow that delegates to
 * pluggable {@link PaymentMethodStrategy} implementations based on the
 * {@link PaymentMethod} discriminator in the initialization command.
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>{@code run()} manages overall workflow lifecycle: it awaits initialization,
 *       delegates to the strategy's {@code awaitAuthorization()}, then waits for a status the
 *       workflow can close on (settlement/cancellation via {@code bookingCompleted} signal)
 *       — see {@link PaymentStatus#isFinal()}.
 *       A configurable expiry timeout with an authorization safety guard prevents
 *       abandoned workflows from running indefinitely.</li>
 *   <li>{@code init()} validates re-initialization policy, delegates to the strategy
 *       for payment-method-specific initialization, and returns the result
 *       synchronously via Temporal Update.</li>
 *   <li>{@code authorize()} (Secure Fields only) validates pre-conditions, sets the
 *       event-inbox flag, and awaits the strategy's authorization result.</li>
 *   <li>{@code webhookReceived()} validates the transaction ID before depositing
 *       the payload into the event-inbox for strategy consumption.</li>
 *   <li>{@code bookingCompleted()} triggers settlement or cancellation based on
 *       the booking outcome.</li>
 * </ol>
 *
 * <h2>Event-Inbox Pattern</h2>
 * <p>Signal handlers validate and deposit events into {@link WorkflowState} inbox fields.
 * Strategies consume these events via {@code Workflow.await(predicate)} — signals are never
 * routed directly to a specific strategy.
 *
 * <h2>Re-initialization Policy Matrix</h2>
 * <ul>
 *   <li>INITIALIZED — allowed (cancel prior transaction if one exists, create new)</li>
 *   <li>authorizationInProgress — reject with AUTHORIZATION_IN_PROGRESS</li>
 *   <li>AUTHORIZED/SETTLED/CANCELLED/EXPIRED — reject (terminal states)</li>
 *   <li>SETTLEMENT_PENDING/SETTLEMENT_FAILED/BOOKING_PENDING_TIMEOUT — reject; an authorization
 *       is outstanding, so a new attempt would hold the customer's money twice</li>
 *   <li>FAILED — allowed (workflow stays alive, new attempt with potentially different method)</li>
 * </ul>
 *
 * <h2>Expiry Window and the Free-Stay Rule</h2>
 * <p>Expiry cancels the Datatrans transaction, so it may only fire while cancelling is
 * unambiguously safe — that is, before a booking can exist. The
 * {@code PaymentAuthorisedEvent} is what triggers the booking downstream, so the expiry window
 * covers the lifecycle up to authorization and no further. Once the payment is AUTHORIZED the
 * workflow leaves the expiry timer behind and waits on the booking outcome instead; a long
 * settlement retry can then outlive the old 30-minute timeout without the timer voiding an
 * authorization under a confirmed booking.
 *
 * <p>The window opens when the workflow starts, not when initialization completes, so it also
 * covers the case where initialization never succeeds at all — both strategies report an init
 * failure as an error result and leave the payment INITIALIZED with no transaction, and nothing
 * else would ever end that wait. Expiring with no transactionId needs no gateway call: there is
 * no Datatrans transaction to cancel and no authorization that could be in flight.
 *
 * <p>Once a transaction does exist the timeout carries its safety guard: after it fires the
 * workflow awaits {@code !authorizationInProgress} before acting, so an authorization started
 * just before expiry is never cancelled underneath the caller.
 *
 * <h2>Booking-Completion Reconciliation</h2>
 * <p>After AUTHORIZED the workflow waits for the {@code bookingCompleted} Kafka signal, polling
 * the Basket Service on each interval that passes without one. Both paths converge on the same
 * settle/cancel decision, and whichever resolves first makes the other's copy redundant.
 *
 * @see PaymentWorkflow
 * @see PaymentMethodStrategy
 * @see PaymentMethodStrategyFactory
 * @see WorkflowState
 */
public class PaymentWorkflowImpl implements PaymentWorkflow {

  private static final Logger log = Workflow.getLogger(PaymentWorkflowImpl.class);

  /**
   * Memo key the adapter stores the configured expiry timeout under.
   *
   * <p>A memo travels in the workflow start event, so reading it is deterministic and replay
   * safe: a history recorded before the memo existed simply yields {@code null} and the
   * workflow falls back to the default in {@link PaymentWorkflowTuning}.
   */
  public static final String EXPIRY_TIMEOUT_MEMO_KEY =
      PaymentWorkflowTuning.EXPIRY_TIMEOUT_MEMO_KEY;

  /**
   * Upper bound on booking-completion polls, independent of the configured horizon.
   *
   * <p>A poll loop that writes to the workflow history on every tick has to be bounded by
   * something the history can carry. The horizon normally ends the loop long before this; the
   * count is here so a misconfigured interval (say, one second) cannot grow the history without
   * limit.
   */
  private static final int MAX_BOOKING_POLLS = 200;

  /**
   * Activity stub with standard retry policy for general operations (reservation lookup,
   * payment method validation, Datatrans initialization, cancellation). Settlement, the
   * authorised-event publish, and the basket-status poll each run on their own stub — see
   * {@link PaymentActivityStubs}, where the retry horizons are set by what each call costs when
   * it fails rather than by one shared default.
   */
  private final PaymentActivities activities = Workflow.newActivityStub(
      PaymentActivities.class,
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(30))
          .setRetryOptions(RetryOptions.newBuilder()
              .setMaximumAttempts(3)
              .build())
          .build()
  );

  private final WorkflowState state = new WorkflowState();
  private final String basketId;

  @WorkflowInit
  public PaymentWorkflowImpl(String basketId) {
    this.basketId = basketId;
  }

  // ==========================================================================
  // @WorkflowMethod — Lifecycle management with expiry and authorization loop
  // ==========================================================================

  /**
   * Main workflow method managing the payment lifecycle.
   *
   * <p>The workflow proceeds through these phases:
   * <ol>
   *   <li>Wait for initialization to complete (transactionId set by {@code init()} update),
   *       expiring if it never does</li>
   *   <li>For each mobile attempt (the first init or any re-init), dispatch the strategy's
   *       {@code awaitAuthorization()}, which blocks until the webhook or reconciliation
   *       resolves it; web attempts are driven by the {@code authorize()} update instead</li>
   *   <li>If AUTHORIZED, wait for {@code bookingCompleted} signal to drive settlement</li>
   *   <li>If FAILED, loop back to allow re-initialization — including a switch of payment
   *       method, which is why the mobile dispatch is per attempt rather than one-time</li>
   * </ol>
   *
   * <p>The pre-authorization lifecycle is bounded by one absolute expiry deadline computed at the
   * top of this method, with an authorization safety guard that prevents cancellation of
   * in-flight authorizations.
   *
   * @param basketId the basket identifier correlating this workflow to a reservation
   */
  @Override
  public void run(String basketId) {
    // The parameter carries the same value as the constructor-injected field (Temporal passes
    // the start argument to both); the field is the single source of truth so every handler,
    // not just this method, reads the one copy.

    // The expiry deadline is absolute and covers the whole pre-authorization lifecycle, waiting
    // for the first init included. It is computed once, here, rather than per phase or per
    // iteration: a failed attempt that the customer retries must not silently extend the window
    // it was already inside, and a session that never initializes at all must still end.
    long expiryDeadline = Workflow.currentTimeMillis() + expiryTimeout().toMillis();

    // Phase 1: Await initialization, bounded by the same deadline.
    // The init() @UpdateMethod sets transactionId and currentMethod, and a transactionId is the
    // only thing that can satisfy this wait: no status transition is reachable before one exists.
    // Both strategies report an init failure as an error result and leave the payment
    // INITIALIZED, and every code path that sets a status either runs after a transactionId was
    // stored or is this method's own timeout below. So an init that keeps failing is answered by
    // the deadline, not by a status.
    boolean initialized = Workflow.await(
        Duration.ofMillis(expiryDeadline - Workflow.currentTimeMillis()),
        () -> state.getTransactionId() != null);

    if (!initialized) {
      // No transactionId was ever created, so no authorization can be in flight and there is no
      // Datatrans transaction to cancel. That is why this path does not go through
      // expireIfSafe(): there is nothing for its safety guard to protect.
      state.setPaymentStatus(PaymentStatus.EXPIRED);
      return;
    }

    // Phase 2+3: Drive each attempt, bounded by the rest of the expiry window.
    //
    // A NEW_CARD_MOBILE attempt is driven by this thread calling the strategy's
    // awaitAuthorization(), which consumes the webhook inbox and runs reconciliation; a
    // NEW_CARD_WEB attempt is driven by the authorize() @UpdateMethod instead, so this loop
    // just waits for its outcome. The mobile dispatch lives INSIDE the loop and keys off the
    // attempt epoch that init() bumps: a payment re-initialized after a failed attempt —
    // web→mobile or mobile retry — gets a fresh driver for its webhook. A one-time dispatch
    // here would strand such an attempt: its webhook would sit in the inbox unconsumed until
    // expiry cancelled a payment the customer had actually completed.
    //
    // The expiry window covers the pre-authorization part of the lifecycle only — an abandoned
    // basket, or a customer who never completed authorization. Expiry cancels the Datatrans
    // transaction, which is safe here precisely because no booking exists yet. The deadline is
    // the one computed at the top of the method; the loop keeps measuring against it rather
    // than starting a fresh timer per iteration or per attempt.
    int drivenInitEpoch = 0;
    while (true) {
      if (mobileAttemptAwaitingDriver(drivenInitEpoch)) {
        drivenInitEpoch = state.getInitEpoch();
        PaymentMethodStrategyFactory.create(state.getCurrentMethod())
            .awaitAuthorization(activities, state);
      }

      long remaining = expiryDeadline - Workflow.currentTimeMillis();
      if (remaining <= 0) {
        expireIfSafe();
        return;
      }

      final int driven = drivenInitEpoch;
      boolean resolved = Workflow.await(Duration.ofMillis(remaining),
          () -> readyForBookingPhase()
              || state.getPaymentStatus().isFinal()
              || mobileAttemptAwaitingDriver(driven));

      if (!resolved) {
        expireIfSafe();
        return;
      }
      if (state.getPaymentStatus().isFinal()) {
        return;
      }
      if (mobileAttemptAwaitingDriver(drivenInitEpoch)) {
        // A new mobile attempt was initialized — loop back to dispatch its driver.
        continue;
      }

      // Phase 4: Authorized. The expiry timer is deliberately out of the picture from here on.
      // Once the PaymentAuthorisedEvent is published a booking may already exist, and cancelling
      // an authorization under a live booking hands out a free stay. Settlement is bounded by
      // the settle activity's schedule-to-close horizon; the booking wait by its poll horizon.
      awaitBookingOutcome();

      if (state.getPaymentStatus().isFinal()) {
        return;
      }
      // The authorization did not survive (an event that could not be published compensated
      // back to FAILED, say). The customer may still re-initialize inside the expiry window.
    }
  }

  /**
   * Returns {@code true} once the payment is authorized and no authorization handler is still
   * running.
   *
   * <p>The {@code authorizationInProgress} half matters: both strategies set AUTHORIZED inside
   * the handler that is still running — the web strategy from the {@code authorize()} update,
   * the mobile strategy from its webhook or reconciliation path — and only clear the flag once
   * that handler returns. Entering the booking phase on the status alone would race a handler
   * that has more to do.
   */
  private boolean readyForBookingPhase() {
    return state.getPaymentStatus() == PaymentStatus.AUTHORIZED
        && !state.isAuthorizationInProgress();
  }

  /**
   * Returns {@code true} when the current attempt is a mobile one this thread has not yet
   * driven — an initialized NEW_CARD_MOBILE payment whose attempt epoch differs from the last
   * epoch {@code run()} dispatched {@code awaitAuthorization()} for.
   *
   * <p>The transactionId guard keeps a failed init out: the strategy only stores a
   * transactionId on success, and a driver started with no transaction would have nothing to
   * await against and nothing that could ever wake it. A failed init leaves the payment
   * INITIALIZED with no transactionId, and the customer's next init bumps the epoch again.
   */
  private boolean mobileAttemptAwaitingDriver(int drivenInitEpoch) {
    return state.getCurrentMethod() == PaymentMethod.NEW_CARD_MOBILE
        && state.getPaymentStatus() == PaymentStatus.INITIALIZED
        && state.getTransactionId() != null
        && state.getInitEpoch() != drivenInitEpoch;
  }

  /**
   * Applies the expiry outcome once the pre-authorization timeout has fired.
   *
   * <p>The authorization safety guard comes first: an authorization that started just before the
   * timeout must be allowed to finish, or the workflow would cancel a transaction underneath the
   * caller who is waiting on it. Only after that, and only if the payment is still unresolved
   * and unauthorized, is the transaction cancelled.
   */
  private void expireIfSafe() {
    // CRITICAL: Never expire while an authorization is in flight.
    Workflow.await(() -> !state.isAuthorizationInProgress());

    // The authorization that was in flight may have driven us somewhere final — or to
    // AUTHORIZED, in which case the booking phase owns the rest of the lifecycle.
    if (readyForBookingPhase()) {
      awaitBookingOutcome();
      return;
    }

    if (state.getPaymentStatus().isFinal()) {
      return;
    }

    cancelIfGatewayAuthorized("expired");
    state.setPaymentStatus(PaymentStatus.EXPIRED);
  }

  /**
   * Cancels the current transaction only when the gateway itself reports it {@code authorized}.
   *
   * <p>Datatrans only accepts a cancel for transactions in {@code authorized} (or
   * {@code settled}) state — an {@code initialized} transaction whose card form was never
   * completed is not cancellable and simply expires at the gateway on its own, so attempting
   * the cancel just burns retries on a deterministic error. The status check also covers the
   * opposite edge: a mobile customer may have genuinely paid while this workflow still says
   * INITIALIZED (webhook not yet processed), and then the gateway reports {@code authorized}
   * and the hold really must be released.
   *
   * <p>Never called for {@code settled}: the two callers (supersede on re-init, expiry) run only
   * pre-authorization from this workflow's point of view, so a settled answer would mean money
   * was captured for a payment this workflow considers unresolved — logged loudly, never
   * cancelled (refunding captured money is an operator decision).
   *
   * <p>Best-effort throughout: any failure is logged and the caller proceeds, matching the
   * previous behavior.
   */
  private void cancelIfGatewayAuthorized(String reason) {
    if (state.getTransactionId() == null || state.getMerchantId() == null) {
      return;
    }
    try {
      // Dedicated stub: a 404 from this check is an answer ("nothing to cancel"), not an
      // outage, and must not burn retries — see PaymentActivityStubs#cancellationCheckActivities.
      DatatransTransactionStatus gatewayStatus = PaymentActivityStubs
          .cancellationCheckActivities()
          .getTransactionStatus(state.getTransactionId(), state.getMerchantId());
      String status = gatewayStatus == null || gatewayStatus.status() == null
          ? "" : gatewayStatus.status().trim().toLowerCase(java.util.Locale.ROOT);

      if ("authorized".equals(status)) {
        activities.cancelTransaction(state.getTransactionId(), state.getMerchantId());
        log.info("Cancelled {} transaction that the gateway had authorized "
                + "[basketId={}, transactionId={}]",
            reason, basketId, state.getTransactionId());
      } else if ("settled".equals(status)) {
        log.error("NOT cancelling {} transaction — the gateway reports it SETTLED while this "
                + "workflow never confirmed an authorization; needs manual review "
                + "[basketId={}, transactionId={}, merchantId={}]",
            reason, basketId, state.getTransactionId(), state.getMerchantId());
      } else {
        log.info("Skipping cancel of {} transaction — gateway status '{}' is not cancellable; "
                + "the transaction lapses at the gateway on its own "
                + "[basketId={}, transactionId={}]",
            reason, status, basketId, state.getTransactionId());
      }
    } catch (RuntimeException e) {
      if (PaymentFailureMapper.hasFailureType(e, TransactionNotFoundException.class)) {
        // The gateway no longer knows the transaction (a never-completed Secure Fields
        // transaction is not even queryable) — there is nothing to cancel. A benign outcome.
        log.info("Skipping cancel of {} transaction — the gateway does not know it "
                + "[basketId={}, transactionId={}]",
            reason, basketId, state.getTransactionId());
        return;
      }
      log.warn("Failed to cancel {} transaction [basketId={}, transactionId={}]",
          reason, basketId, state.getTransactionId(), e);
    }
  }

  // ==========================================================================
  // @UpdateMethod — Payment initialization with re-init policy matrix
  // ==========================================================================

  /**
   * Initializes a payment session with Datatrans for the given payment method.
   *
   * <p>Implements the re-initialization policy matrix:
   * <ul>
   *   <li>If {@code authorizationInProgress} — reject with AUTHORIZATION_IN_PROGRESS</li>
   *   <li>If AUTHORIZED/SETTLED/CANCELLED/EXPIRED — reject (terminal states)</li>
   *   <li>If FAILED — allow (reset state, new attempt)</li>
   *   <li>If INITIALIZED — allow (cancel prior transaction if exists, create new)</li>
   * </ul>
   *
   * @param command the polymorphic initialization command
   * @return the initialization result containing transactionId on success or error details
   */
  @Override
  public PaymentInitResult init(PaymentInitCommand command) {
    // Re-initialization policy: reject while authorization is in progress
    if (state.isAuthorizationInProgress()) {
      return new PaymentInitResult(false, null,
          PaymentErrorCode.AUTHORIZATION_IN_PROGRESS,
          PaymentErrorCode.AUTHORIZATION_IN_PROGRESS.getErrorMessage());
    }

    PaymentStatus currentStatus = state.getPaymentStatus();

    // Re-initialization policy: reject while the customer's funds are held. SETTLEMENT_PENDING,
    // SETTLEMENT_FAILED, and BOOKING_PENDING_TIMEOUT all sit on a live authorization, so a new
    // attempt would put a second hold on the same card for the same stay. Unlike FAILED, these
    // are not the customer's to retry — they need an operator, not a new transaction.
    if (currentStatus.holdsFunds()) {
      return new PaymentInitResult(false, null,
          PaymentErrorCode.INVALID_TRANSACTION_STATE,
          "Payment is held pending settlement reconciliation: " + currentStatus);
    }

    // Re-initialization policy: reject terminal states (except FAILED)
    if (currentStatus == PaymentStatus.AUTHORIZED
        || currentStatus == PaymentStatus.SETTLED
        || currentStatus == PaymentStatus.CANCELLED) {
      return new PaymentInitResult(false, null,
          PaymentErrorCode.INVALID_TRANSACTION_STATE,
          "Payment is in terminal state: " + currentStatus);
    }

    if (currentStatus == PaymentStatus.EXPIRED) {
      return new PaymentInitResult(false, null,
          PaymentErrorCode.EXPIRED,
          PaymentErrorCode.EXPIRED.getErrorMessage());
    }

    // Re-initialization policy: INITIALIZED with existing transaction or FAILED — release the
    // prior transaction before creating a new one. Cancelled only if the gateway reports it
    // authorized (Datatrans rejects cancels in any earlier state; an initialized transaction
    // simply expires there on its own). Non-fatal either way: the new attempt proceeds.
    cancelIfGatewayAuthorized("superseded");

    // Reset state for the new attempt. The epoch bump is what tells run() this is a fresh
    // attempt, so a re-initialized mobile payment gets its awaitAuthorization() driver
    // dispatched again — see mobileAttemptAwaitingDriver().
    state.reset();
    state.setCurrentMethod(command.paymentMethod());
    state.setInitEpoch(state.getInitEpoch() + 1);

    // Delegate to the appropriate strategy
    PaymentMethodStrategy strategy = PaymentMethodStrategyFactory.create(command.paymentMethod());
    return strategy.init(command, activities, state);
  }

  // ==========================================================================
  // @UpdateMethod — Synchronous authorization with pre-condition validation
  // ==========================================================================

  /**
   * Authorizes the previously initialized payment, returning the result synchronously.
   *
   * <p>Pre-condition validation (fail-fast):
   * <ul>
   *   <li>{@code transactionId != null} — payment must be initialized</li>
   *   <li>{@code !authorizationInProgress} — no concurrent authorization</li>
   *   <li>{@code paymentStatus == INITIALIZED} — valid state for authorization</li>
   *   <li>{@code currentMethod == NEW_CARD_WEB} — mobile attempts are driven by {@code run()},
   *       and a second processor on the same transaction could double-publish the
   *       authorised event</li>
   * </ul>
   *
   * <p>After pre-conditions pass, sets {@code authorizeSignalReceived = true} in the
   * event-inbox and blocks until the strategy produces an {@link AuthorizeResult} or the
   * attempt fails. The strategy's {@code awaitAuthorization()} method (running in the
   * {@code run()} thread) consumes the flag and performs the actual Datatrans authorization.
   *
   * @return the authorization result (success or typed error)
   */
  @Override
  public AuthorizeResult authorize() {
    // Pre-condition: payment must be initialized (transactionId exists)
    if (state.getTransactionId() == null) {
      return new AuthorizeResult(false,
          PaymentErrorCode.INVALID_TRANSACTION_STATE,
          "Payment not initialized");
    }

    // Pre-condition: no concurrent authorization
    if (state.isAuthorizationInProgress()) {
      return new AuthorizeResult(false,
          PaymentErrorCode.AUTHORIZATION_IN_PROGRESS,
          PaymentErrorCode.AUTHORIZATION_IN_PROGRESS.getErrorMessage());
    }

    // Pre-condition: payment must be in INITIALIZED state
    if (state.getPaymentStatus() != PaymentStatus.INITIALIZED) {
      return new AuthorizeResult(false,
          PaymentErrorCode.INVALID_TRANSACTION_STATE,
          "Payment not in valid state for authorization: " + state.getPaymentStatus());
    }

    // Pre-condition: only NEW_CARD_WEB is driven by this update. A mobile attempt already has
    // its processor dispatched by run(); accepting authorize() here would start a SECOND
    // processor on the same transaction, and both could publish PaymentAuthorisedEvent for one
    // payment (the INITIALIZED re-check does not exclude them, because the status only flips
    // to AUTHORIZED after the publish activity completes).
    if (state.getCurrentMethod() != PaymentMethod.NEW_CARD_WEB) {
      return new AuthorizeResult(false,
          PaymentErrorCode.INVALID_TRANSACTION_STATE,
          "authorize is not supported for payment method: " + state.getCurrentMethod());
    }

    // Pre-conditions passed — mark authorization as in progress
    state.setAuthorizationInProgress(true);
    state.setAuthorizeSignalReceived(true);

    // Delegate to the strategy's awaitAuthorization which performs the actual
    // Datatrans authorization and sets the authorizeResult in WorkflowState.
    // For NEW_CARD_WEB: the strategy was already awaiting authorizeSignalReceived,
    // now it proceeds to call the authorize activity.
    PaymentMethodStrategy strategy = PaymentMethodStrategyFactory.create(state.getCurrentMethod());
    strategy.awaitAuthorization(activities, state);

    state.setAuthorizationInProgress(false);

    // Return the result from the strategy
    if (state.getAuthorizeResult() != null) {
      return state.getAuthorizeResult();
    }

    // Fallback: attempt failed without producing a result
    return new AuthorizeResult(false,
        PaymentErrorCode.GATEWAY_ERROR,
        "Authorization attempt failed");
  }

  // ==========================================================================
  // @SignalMethod — Secure webhook handling with transactionId validation
  // ==========================================================================

  /**
   * Receives a webhook payload from the payment gateway.
   *
   * <p>Implements the event-inbox pattern with security validation:
   * <ul>
   *   <li>Validates {@code payload.transactionId()} matches stored transaction ID</li>
   *   <li>Mismatched payloads are logged and dropped (replay-attack guard)</li>
   *   <li>Only validated payloads are deposited into the event-inbox</li>
   * </ul>
   *
   * <p>Strategies consume the deposited webhook via
   * {@code Workflow.await(() -> state.getWebhookPayload() != null)}.
   *
   * @param payload the gateway-scoped webhook payload
   */
  @Override
  public void webhookReceived(WebhookPayload payload) {
    if (payload == null) {
      return;
    }

    // CRITICAL: Validate transactionId BEFORE depositing to inbox.
    // Mismatched payloads are logged and dropped — never deposited.
    if (state.getTransactionId() == null
        || !state.getTransactionId().equals(payload.transactionId())) {
      log.warn("Webhook transactionId mismatch [basketId={}, expected={}, received={}]",
          basketId, state.getTransactionId(), payload.transactionId());
      return;
    }

    // Deposit validated payload into event-inbox for strategy consumption
    state.setWebhookPayload(payload);
  }

  // ==========================================================================
  // @SignalMethod — BookingCompletedEvent for settlement/cancellation
  // ==========================================================================

  /**
   * Signals the workflow when a {@link BookingCompletedEvent} is received from Kafka.
   *
   * <p>The handler deposits the event into the inbox and returns; the {@code run()} thread acts
   * on it. Keeping the settle or cancel out of the signal handler is what makes the polling
   * safety net safe: exactly one thread decides the booking outcome, so a signal that arrives
   * while a poll is already in flight cannot start a second settlement.
   *
   * <p>The state guard drops signals that arrive too early (before AUTHORIZED) and too late
   * (after polling already resolved the outcome). A late signal is expected, not exceptional:
   * the poller and Kafka are racing to report the same fact, and once either wins the other's
   * copy is redundant. Even if one slipped through, the settle converges rather than
   * double-capturing.
   *
   * @param event the booking completed event containing basketReference and status
   */
  @Override
  public void bookingCompleted(BookingCompletedEvent event) {
    // State guard: only accept while awaiting the booking outcome in AUTHORIZED state.
    if (state.getPaymentStatus() != PaymentStatus.AUTHORIZED) {
      log.info("Ignoring BookingCompletedEvent — booking outcome no longer awaited "
              + "[basketId={}, eventStatus={}, paymentStatus={}]",
          basketId, event != null ? event.status() : "null", state.getPaymentStatus());
      return;
    }

    if (state.getBookingCompletedEvent() != null) {
      log.info("Ignoring duplicate BookingCompletedEvent [basketId={}, eventStatus={}]",
          basketId, event != null ? event.status() : "null");
      return;
    }

    if (event == null) {
      // A null event is not "no news" — it is an unreadable outcome, and the fail-safe for an
      // unreadable outcome before any booking is confirmed is to release the money.
      log.warn("Received null BookingCompletedEvent [basketId={}]. "
          + "Defaulting to cancellation.", basketId);
      state.setBookingCompletedEvent(new BookingCompletedEvent(basketId, "FAILED"));
      return;
    }

    state.setBookingCompletedEvent(event);
  }

  // ==========================================================================
  // @QueryMethod — Status queries
  // ==========================================================================

  @Override
  public PaymentStatus getPaymentStatus() {
    return state.getPaymentStatus();
  }

  @Override
  public AuthorizeResult getAuthorizeResult() {
    return state.getAuthorizeResult();
  }

  // ==========================================================================
  // Private — Expiry configuration
  // ==========================================================================

  /**
   * Resolves the workflow expiry timeout from the start memo the adapter sets from
   * {@code integrations.payment.workflow.timeout}, falling back to the default when the memo
   * is absent (older histories, or a client that did not set it).
   */
  private Duration expiryTimeout() {
    return PaymentWorkflowTuning.expiryTimeout();
  }

  // ==========================================================================
  // Private — Booking-completion reconciliation
  // ==========================================================================

  /**
   * Waits for the booking outcome, then settles or cancels accordingly.
   *
   * <p>The {@code bookingCompleted} Kafka signal is the fast path and usually the only one that
   * runs. It is not, however, a path this workflow controls: the event crosses a broker, a
   * consumer group, and a signal delivery, and any of those can lose or delay it. An authorized
   * payment whose signal never arrives holds the guest's money with nobody watching, so each
   * interval that passes without a signal is answered by asking the Basket Service directly what
   * happened to the basket.
   *
   * <p>The loop is written as {@code Workflow.await(interval, predicate)} rather than a sleep so
   * a signal that arrives mid-interval is acted on immediately, and so the history records one
   * timer per interval rather than one per check.
   *
   * <p>Exhausting the horizon with the basket still pending is the one genuinely ambiguous
   * outcome, and it is the reason this method does not simply cancel on timeout — see
   * {@link PaymentStatus#BOOKING_PENDING_TIMEOUT}.
   */
  private void awaitBookingOutcome() {
    Duration pollInterval = PaymentWorkflowTuning.bookingPollInterval();
    long deadline = Workflow.currentTimeMillis()
        + PaymentWorkflowTuning.bookingPollHorizon().toMillis();

    PaymentActivities basketStatusActivities = PaymentActivityStubs.basketStatusActivities();
    int polls = 0;

    while (state.getPaymentStatus() == PaymentStatus.AUTHORIZED) {
      Workflow.await(pollInterval, () ->
          state.getBookingCompletedEvent() != null
              || state.getPaymentStatus() != PaymentStatus.AUTHORIZED);

      if (state.getPaymentStatus() != PaymentStatus.AUTHORIZED) {
        return;
      }

      BookingCompletedEvent event = state.getBookingCompletedEvent();
      if (event != null) {
        if (event.isCompleted()) {
          settleAuthorizedPayment();
        } else {
          cancelAuthorizedPayment();
        }
        return;
      }

      if (Workflow.currentTimeMillis() >= deadline || polls >= MAX_BOOKING_POLLS) {
        parkOnBookingTimeout(polls);
        return;
      }

      polls++;
      BasketStatus basketStatus;
      try {
        basketStatus = basketStatusActivities.getBasketStatus(basketId);
      } catch (RuntimeException e) {
        // A failed read is not an outcome. The next interval is the retry.
        log.warn("Basket status poll failed [basketId={}, attempt={}, error={}]",
            basketId, polls, e.getMessage());
        continue;
      }

      // The activity yielded to Temporal — the signal may have won the race while it was in
      // flight, in which case the next loop pass consumes it rather than this poll's answer.
      if (state.getPaymentStatus() != PaymentStatus.AUTHORIZED
          || state.getBookingCompletedEvent() != null) {
        continue;
      }

      BasketStatus resolvedStatus = basketStatus == null ? BasketStatus.UNKNOWN : basketStatus;
      if (resolvedStatus.isBookingCompleted()) {
        log.info("Booking completion resolved by basket poll [basketId={}, basketStatus={}, "
            + "attempt={}]", basketId, resolvedStatus, polls);
        settleAuthorizedPayment();
        return;
      }
      if (resolvedStatus.isBookingFailed()) {
        log.info("Booking failure resolved by basket poll [basketId={}, basketStatus={}, "
            + "attempt={}]", basketId, resolvedStatus, polls);
        cancelAuthorizedPayment();
        return;
      }

      log.info("Booking still pending [basketId={}, basketStatus={}, attempt={}]",
          basketId, resolvedStatus, polls);
    }
  }

  /**
   * Parks an authorized payment whose booking outcome never became knowable.
   *
   * <p>Neither automatic action is defensible here. Cancelling would void the hold on a booking
   * that may still complete — a free stay. Settling would charge for a booking that may never
   * exist. So the workflow stops, keeps the authorization intact, and says loudly enough for an
   * operator to find it.
   *
   * <p>The park ends the execution by <em>failing</em> it rather than returning. A workflow that
   * returns shows as Completed in the Temporal UI, indistinguishable at a glance from the
   * thousands of settled payments around it — and this payment is holding a customer's money
   * with a finite window (card holds lapse after days) for a human to act. Failing the execution
   * with a typed {@link ApplicationFailure} makes parked payments filterable in the UI and
   * countable in workflow-failure metrics. The payment status stays queryable on the failed
   * execution, so the status endpoint is unaffected.
   */
  private void parkOnBookingTimeout(int polls) {
    state.setPaymentStatus(PaymentStatus.BOOKING_PENDING_TIMEOUT);
    log.error("MANUAL RECONCILIATION REQUIRED — booking outcome unknown after polling horizon; "
            + "authorization left intact and NOT cancelled. Confirm the booking in the Basket "
            + "Service and settle or cancel in the Datatrans dashboard "
            + "[basketId={}, transactionId={}, refno={}, amount={} {}, merchantId={}, polls={}]",
        basketId, state.getTransactionId(), state.getBookingReference(),
        state.getAmount(), state.getCurrency(), state.getMerchantId(), polls);
    throw ApplicationFailure.newNonRetryableFailure(
        "Booking outcome unknown after the polling horizon; the authorization is held intact "
            + "and needs manual reconciliation [basketId=" + basketId
            + ", transactionId=" + state.getTransactionId() + "]",
        PaymentStatus.BOOKING_PENDING_TIMEOUT.name());
  }

  // ==========================================================================
  // Private — Settlement and cancellation
  // ==========================================================================

  /**
   * Settles the authorized payment via Datatrans after a confirmed booking.
   *
   * <p>Runs on {@link PaymentActivityStubs#settlementActivities()}, whose retry horizon is
   * measured in days: at this point the guest has a booking they expect to be charged for, and
   * a gateway outage is not a reason to hand out a free stay. A retry cannot capture twice
   * because the settle converges against the gateway's transaction status.
   *
   * <p>{@code SETTLEMENT_PENDING} is set before the activity is scheduled so a query made during
   * a long retry says what is actually happening rather than leaving the payment looking merely
   * {@code AUTHORIZED}.
   */
  private void settleAuthorizedPayment() {
    state.setPaymentStatus(PaymentStatus.SETTLEMENT_PENDING);
    try {
      PaymentActivityStubs.settlementActivities().settleTransaction(
          state.getTransactionId(),
          state.getAmount(),
          state.getCurrency(),
          state.getBookingReference(),
          state.getMerchantId());
      state.setPaymentStatus(PaymentStatus.SETTLED);
      log.info("Payment settled [basketId={}, transactionId={}]",
          basketId, state.getTransactionId());
    } catch (RuntimeException e) {
      // The horizon is exhausted or the failure was terminal. The booking exists and the money
      // is authorized, so the authorization is left alone — cancelling it here would be the
      // free-stay bug this whole design exists to avoid.
      state.setPaymentStatus(PaymentStatus.SETTLEMENT_FAILED);
      log.error("MANUAL SETTLEMENT REQUIRED — settlement exhausted its retry horizon and the "
              + "booking is confirmed; the authorization has NOT been cancelled. Settle this "
              + "transaction manually in the Datatrans dashboard "
              + "[basketId={}, transactionId={}, refno={}, amount={} {}, merchantId={}, "
              + "error={}]",
          basketId, state.getTransactionId(), state.getBookingReference(),
          state.getAmount(), state.getCurrency(), state.getMerchantId(), e.getMessage(), e);
      // Fail the execution rather than return: a parked payment must not show as Completed in
      // the Temporal UI — see parkOnBookingTimeout for the reasoning.
      throw ApplicationFailure.newNonRetryableFailure(
          "Settlement exhausted its retry horizon with the booking confirmed; the authorization "
              + "is held intact and needs manual capture [basketId=" + basketId
              + ", transactionId=" + state.getTransactionId() + "]",
          PaymentStatus.SETTLEMENT_FAILED.name());
    }
  }

  /**
   * Cancels the authorized payment via Datatrans after a failed booking or unknown status.
   */
  private void cancelAuthorizedPayment() {
    try {
      activities.cancelTransaction(state.getTransactionId(), state.getMerchantId());
      state.setPaymentStatus(PaymentStatus.CANCELLED);
      log.info("Payment cancelled [basketId={}, transactionId={}]",
          basketId, state.getTransactionId());
    } catch (RuntimeException e) {
      log.error("Cancellation failed [basketId={}, transactionId={}, error={}]",
          basketId, state.getTransactionId(), e.getMessage(), e);
      state.setPaymentStatus(PaymentStatus.FAILED);
    }
  }
}
