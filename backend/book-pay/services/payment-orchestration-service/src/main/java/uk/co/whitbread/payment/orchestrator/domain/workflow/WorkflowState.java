package uk.co.whitbread.payment.orchestrator.domain.workflow;

import lombok.Data;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.MobileSdkReconciliationSettings;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

/**
 * Mutable workflow state consolidating fields from both Secure Fields and Mobile SDK
 * payment workflow implementations.
 *
 * <p>This class serves as the single source of truth for a running payment workflow,
 * holding transaction state, event-inbox fields for the strategy pattern, and
 * reconciliation data for Mobile SDK flows.
 *
 * <p>Event-inbox fields ({@code authorizeSignalReceived}, {@code webhookPayload},
 * {@code bookingCompletedEvent}) are deposited by signal handlers and consumed by
 * strategies via {@code Workflow.await(predicate)}.
 */
@Data
public class WorkflowState {

  // --- Core payment state ---
  private PaymentStatus paymentStatus = PaymentStatus.INITIALIZED;
  private String transactionId;
  private String merchantId;
  private long amount;
  private String currency;
  // The basket correlation key: the basketId the client sent on init. It becomes
  // PaymentAuthorisedEvent.basketId AND the Kafka message key, so it must never carry the
  // reservation service's own reservationId (a different identifier, see ReservationByIdDto).
  private String basketId;
  private String bookingReference;
  private String language;
  private AuthorizeResult authorizeResult;

  // --- Strategy-specific state ---
  private PaymentMethod currentMethod;
  private boolean authorizationInProgress;
  private boolean attemptFailed;

  /**
   * Counts payment attempts: bumped by every {@code init()} that passes the re-initialization
   * policy. The {@code run()} thread compares it against the last attempt it dispatched a
   * mobile driver for, so a re-initialized NEW_CARD_MOBILE attempt (cross-method or retry)
   * gets its {@code awaitAuthorization()} started even though {@code run()} only passes its
   * one-time dispatch point once. Deliberately survives {@link #reset()} — reset marks a new
   * attempt, which is exactly what this field counts.
   */
  private int initEpoch;

  // --- Event-Inbox Fields (consumed by strategies via Workflow.await) ---
  private boolean authorizeSignalReceived;
  private WebhookPayload webhookPayload;
  private BookingCompletedEvent bookingCompletedEvent;

  // --- NEW_CARD_MOBILE specific (reconciliation) ---
  private MobileSdkReconciliationSettings reconciliationSettings;
  private long initCompletedAtMillis;
  private int pollAttemptCount;

  // --- Authorization outcome fields ---
  private String cardAlias;
  private long authorizedAmount;
  private String acquirerAuthorizationCode;

  /**
   * Resets mutable state for re-initialization while preserving the workflow identity.
   *
   * <p>Called when a payment is re-initialized (same-method or cross-method) after a
   * failed attempt. The workflow's own basket identity (the {@code @WorkflowInit} field in
   * {@code PaymentWorkflowImpl}) is immutable; the {@code basketId} here is per-attempt
   * plumbing for the authorised event and is reset with the rest.
   */
  public void reset() {
    this.paymentStatus = PaymentStatus.INITIALIZED;
    this.transactionId = null;
    this.merchantId = null;
    this.amount = 0;
    this.currency = null;
    this.basketId = null;
    this.bookingReference = null;
    this.language = null;
    this.authorizeResult = null;
    this.currentMethod = null;
    this.authorizationInProgress = false;
    this.attemptFailed = false;
    this.authorizeSignalReceived = false;
    this.webhookPayload = null;
    this.bookingCompletedEvent = null;
    this.reconciliationSettings = null;
    this.initCompletedAtMillis = 0;
    this.pollAttemptCount = 0;
    this.cardAlias = null;
    this.authorizedAmount = 0;
    this.acquirerAuthorizationCode = null;
    // initEpoch is intentionally NOT reset: it counts attempts across resets.
  }
}
