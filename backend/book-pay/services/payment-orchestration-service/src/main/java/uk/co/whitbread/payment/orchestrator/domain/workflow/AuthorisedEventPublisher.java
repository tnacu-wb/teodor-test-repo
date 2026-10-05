package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentOption;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;

/**
 * Publishes the {@code PaymentAuthorisedEvent} that triggers the booking, and compensates when
 * it cannot be published.
 *
 * <p>Both payment strategies call this at the same point and in the same order: the gateway has
 * held the money, the booking has not started, and the payment is not yet marked AUTHORIZED —
 * each strategy sets that only once this returns {@code true}. The event is what starts it, so an unpublished event
 * leaves an authorization attached to nothing at all — the guest's money held for a stay that
 * will never be booked.
 *
 * <p>That is also what makes the failure recoverable. No booking exists yet, so the compensation
 * is complete: cancel the authorization and fail the attempt, which lets the customer pay again
 * from a clean state. This is the one place in the settlement design where cancelling is the
 * right answer — after the event succeeds, it stops being one.
 */
final class AuthorisedEventPublisher {

  private static final Logger log = Workflow.getLogger(AuthorisedEventPublisher.class);

  private AuthorisedEventPublisher() {
  }

  /**
   * Publishes the authorised-payment event, compensating on exhaustion.
   *
   * <p>Retries run on {@link PaymentActivityStubs#publishActivities()} — a short horizon,
   * because a customer is watching a pending screen while this happens.
   *
   * @param compensationActivities stub used to cancel the authorization if the publish fails
   * @param state the workflow state carrying the transaction and card data
   * @param authorizedAmount the authorized amount in minor units
   * @param paymentMethod the Datatrans payment method code, may be {@code null}
   * @param last4Digits the masked card's last four digits, may be {@code null}
   * @param expiry the card expiry in "MM/YY" format, may be {@code null}
   * @return {@code true} if the event was published; {@code false} if it was not and the
   *     authorization has been compensated (state is now {@code FAILED})
   */
  static boolean publish(PaymentActivities compensationActivities, WorkflowState state,
      long authorizedAmount, String paymentMethod, String last4Digits, String expiry) {
    try {
      PaymentActivityStubs.publishActivities().publishAuthorisedPaymentEvent(
          state.getBasketId(),
          state.getTransactionId(),
          state.getCardAlias(),
          authorizedAmount,
          state.getCurrency(),
          paymentMethod,
          last4Digits,
          expiry,
          PaymentOption.PAY_NOW,
          state.getLanguage());
      return true;
    } catch (RuntimeException e) {
      log.error("Authorised-payment event could not be published within its horizon; "
              + "cancelling the authorization so no money is held against a booking that will "
              + "never start [transactionId={}, error={}]",
          state.getTransactionId(), e.getMessage(), e);
      compensate(compensationActivities, state);
      return false;
    }
  }

  /**
   * Releases the authorization and fails the attempt.
   *
   * <p>A failed cancellation is logged rather than rethrown: the payment has already failed from
   * the customer's point of view, and losing the failure result on top of it would leave the
   * caller with no answer at all. The stranded hold expires at the gateway and is visible in the
   * ERROR log for reconciliation.
   */
  private static void compensate(PaymentActivities activities, WorkflowState state) {
    if (state.getTransactionId() != null && state.getMerchantId() != null) {
      try {
        activities.cancelTransaction(state.getTransactionId(), state.getMerchantId());
      } catch (RuntimeException e) {
        log.error("Failed to cancel the authorization after an unpublishable authorised-payment "
                + "event — the hold is stranded and needs manual review "
                + "[transactionId={}, merchantId={}, error={}]",
            state.getTransactionId(), state.getMerchantId(), e.getMessage(), e);
      }
    }

    state.setPaymentStatus(PaymentStatus.FAILED);
    state.setAttemptFailed(true);
    state.setAuthorizeResult(new AuthorizeResult(false,
        PaymentErrorCode.GATEWAY_ERROR,
        "Payment could not be completed — please try again"));
  }
}
