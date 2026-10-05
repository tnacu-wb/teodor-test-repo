package uk.co.whitbread.payment.orchestrator.domain.ports.secondary;

import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;

/**
 * Secondary port abstracting Temporal workflow client operations.
 *
 * <p>Defines the outbound contract for starting or updating a payment
 * workflow and retrieving the initialization result.
 */
public interface PaymentWorkflowPort {

  /**
   * Initialize a payment session using the unified workflow.
   *
   * <p>Uses Update-With-Start to start a PaymentWorkflow if not running or update
   * an existing one. Accepts a polymorphic command and returns a typed result.
   *
   * @param command the payment initialization command (web or mobile)
   * @return the initialization result containing transactionId or error details
   */
  PaymentInitResult initPayment(PaymentInitCommand command);

  /**
   * Authorize a payment via the unified workflow's {@code @UpdateMethod authorize()}.
   *
   * <p>The update is durably accepted by the workflow, then its result is awaited for a bounded
   * period. If the handler has not finished by then the workflow keeps running and the returned
   * result carries {@link uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode
   * #AUTHORIZATION_PENDING} — a "not yet", not a failure. The caller must poll
   * {@link #getPaymentStatus(String)} for the outcome and must not retry the authorization.
   *
   * @param basketId the basket identifier (used to derive the workflow ID)
   * @return the authorize result indicating success, failure, or still-pending
   */
  AuthorizeResult authorizePayment(String basketId);

  /**
   * Read the current payment status, and the authorization outcome when one exists.
   *
   * <p>Served by workflow queries, so it never mutates the workflow and is safe to poll.
   *
   * @param basketId the basket identifier (used to derive the workflow ID)
   * @return the current status snapshot
   * @throws uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException if no
   *     payment workflow exists for the basket
   */
  PaymentStatusResponse getPaymentStatus(String basketId);

  /**
   * Signal the webhook event to the payment workflow.
   *
   * @param basketId the basket identifier (used to derive the workflow ID)
   * @param payload  the webhook payload from Datatrans
   */
  void signalWebhookReceived(String basketId, WebhookPayload payload);
}
