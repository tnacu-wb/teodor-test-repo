package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.UpdateMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

/**
 * Unified Temporal workflow interface for payment orchestration.
 *
 * <p>Replaces the separate {@code SecureFieldsPaymentWorkflow} and
 * {@code MobileSdkPaymentWorkflow} with a single generic interface that supports all
 * payment methods (web Secure Fields, mobile SDK, and future methods like Google Pay,
 * Apple Pay, saved cards) through a polymorphic {@link PaymentInitCommand} and pluggable
 * strategy pattern.
 *
 * <h2>Authorize semantics</h2>
 * <p>The {@link #authorize()} method is an {@code @UpdateMethod} that returns
 * {@link AuthorizeResult} synchronously. The handler blocks (via {@code Workflow.await})
 * until the underlying strategy completes authorization or fails. This eliminates the
 * previous signal-then-poll pattern and gives the caller an immediate result.
 *
 * <p>Pre-condition validation in the {@code authorize} implementation must enforce:
 * <ul>
 *   <li>A transaction ID exists (payment has been initialized)</li>
 *   <li>No authorization is already in progress</li>
 *   <li>The payment status is {@code INITIALIZED}</li>
 * </ul>
 * If any pre-condition fails, the handler returns an error {@link AuthorizeResult}
 * immediately without awaiting the strategy.
 *
 * <h2>Event-inbox pattern</h2>
 * <p>Signal handlers ({@link #webhookReceived(WebhookPayload)} and
 * {@link #bookingCompleted(BookingCompletedEvent)}) deposit validated events into
 * workflow state fields. Strategies consume these events via
 * {@code Workflow.await(predicate)} — signals are never routed directly to a specific
 * strategy. This decouples signal handling from method-specific logic and keeps the
 * workflow deterministic for replay.
 *
 * <h2>Polymorphic initialization</h2>
 * <p>The {@link #init(PaymentInitCommand)} update accepts any subtype of the sealed
 * {@link PaymentInitCommand} hierarchy. The workflow delegates to the appropriate
 * {@code PaymentMethodStrategy} based on the command's {@code paymentMethod()}
 * discriminator, allowing new payment methods to be added without modifying this
 * interface.
 */
@WorkflowInterface
public interface PaymentWorkflow {

  /**
   * Main workflow method. Keeps the workflow running to receive signals and updates
   * until the payment reaches a status the workflow can close on
   * ({@code PaymentStatus#isFinal()}) or the workflow expires.
   *
   * <p>The implementation should use {@code Workflow.await(timeout, predicate)} to
   * manage workflow expiry with an authorization safety guard — never expiring while
   * an authorization is in flight.
   *
   * @param basketId the basket identifier correlating this workflow to a reservation
   */
  @WorkflowMethod
  void run(String basketId);

  /**
   * Initializes a payment session with Datatrans for the given payment method.
   *
   * <p>Accepts any subtype of {@link PaymentInitCommand} and delegates to the
   * corresponding {@code PaymentMethodStrategy} for method-specific initialization.
   * Uses Update-With-Start semantics so that calling this update also starts the
   * workflow if it does not already exist.
   *
   * @param command the polymorphic initialization command
   * @return the initialization result containing transactionId on success or error details
   */
  @UpdateMethod
  PaymentInitResult init(PaymentInitCommand command);

  /**
   * Authorizes the previously initialized payment, returning the result synchronously.
   *
   * <p>This is an {@code @UpdateMethod} with blocking semantics: the handler validates
   * pre-conditions, sets {@code authorizeSignalReceived = true} in the event inbox, then
   * awaits until the strategy produces an {@link AuthorizeResult} or the attempt fails.
   *
   * <p><strong>Pre-conditions checked (fail-fast):</strong>
   * <ul>
   *   <li>{@code transactionId != null} — payment must be initialized</li>
   *   <li>{@code !authorizationInProgress} — no concurrent authorization</li>
   *   <li>{@code paymentStatus == INITIALIZED} — valid state for authorization</li>
   * </ul>
   *
   * @return the authorization result (success or typed error)
   */
  @UpdateMethod
  AuthorizeResult authorize();

  /**
   * Receives a validated webhook payload from the payment gateway.
   *
   * <p>Implements the event-inbox pattern: the signal handler validates that
   * {@code payload.transactionId()} matches the stored transaction ID before depositing
   * into the inbox. Mismatched payloads are logged and dropped — they are never deposited,
   * serving as a replay-attack guard.
   *
   * <p>Strategies consume the deposited webhook via
   * {@code Workflow.await(() -> webhookPayload != null)}.
   *
   * @param payload the gateway-scoped webhook payload (discriminated by gateway type)
   */
  @SignalMethod
  void webhookReceived(WebhookPayload payload);

  /**
   * Signals the workflow when a {@link BookingCompletedEvent} is received from Kafka.
   *
   * <p>Called by the {@code PaymentWorkflowSignaler} after consuming an event from the
   * {@code booking-completed} topic. This signal is only processed when the workflow is
   * in the {@code AUTHORIZED} state; signals received in any other state are ignored.
   *
   * <p>Based on the event status:
   * <ul>
   *   <li>{@code COMPLETED} — triggers settlement via Datatrans</li>
   *   <li>{@code FAILED} (or unknown) — triggers cancellation of the authorized payment</li>
   * </ul>
   *
   * @param event the booking completed event containing basketReference and status
   */
  @SignalMethod
  void bookingCompleted(BookingCompletedEvent event);

  /**
   * Queries the current payment status of this workflow.
   *
   * @return the current {@link PaymentStatus}
   */
  @QueryMethod
  PaymentStatus getPaymentStatus();

  /**
   * Queries the result of the most recent authorization attempt.
   *
   * <p>This query is optional and intended for status checks — the primary mechanism
   * for obtaining authorization results is the synchronous {@link #authorize()} update.
   * Returns {@code null} if no authorization has been processed yet.
   *
   * @return the {@link AuthorizeResult}, or {@code null} if no authorization has completed
   */
  @QueryMethod
  AuthorizeResult getAuthorizeResult();
}
