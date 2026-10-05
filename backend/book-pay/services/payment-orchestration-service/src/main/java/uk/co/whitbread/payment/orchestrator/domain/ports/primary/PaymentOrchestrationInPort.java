package uk.co.whitbread.payment.orchestrator.domain.ports.primary;

import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;

/**
 * Primary port for coordinating payment flows.
 *
 * <p>Defines the inbound use cases for unified payment orchestration across
 * Secure Fields (web) and Mobile SDK (native) channels via a single polymorphic interface.
 */
public interface PaymentOrchestrationInPort {

  /**
   * Initialize a payment session for the given basket using the specified payment method.
   *
   * <p>Accepts a polymorphic {@link PaymentInitCommand} whose concrete subtype determines
   * the Datatrans integration type (Secure Fields or Mobile SDK).
   *
   * @param command the payment initialization command (web or mobile)
   * @return the initialization result containing transactionId on success, or error details
   */
  PaymentInitResult initPayment(PaymentInitCommand command);

  /**
   * Authorize a payment transaction for the given basket.
   *
   * <p>Calls the workflow's {@code @UpdateMethod authorize()} and waits a bounded time for its
   * result. The workflow uses its internally stored transactionId from initialization.
   *
   * <p>When the wait elapses the authorization is still running in the workflow and the result
   * carries the {@code AUTHORIZATION_PENDING} error code — the caller should poll
   * {@link #getPaymentStatus(String)} rather than retry.
   *
   * @param basketId the basket identifier
   * @return the authorization result indicating success, failure, or still-pending
   */
  AuthorizeResult authorizePayment(String basketId);

  /**
   * Read the current payment status for the given basket, plus the authorization outcome when
   * one exists.
   *
   * @param basketId the basket identifier
   * @return the current status snapshot
   */
  PaymentStatusResponse getPaymentStatus(String basketId);
}
