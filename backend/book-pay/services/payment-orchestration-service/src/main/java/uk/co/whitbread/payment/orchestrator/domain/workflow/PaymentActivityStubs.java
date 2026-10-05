package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import java.time.Duration;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.DatatransAuthenticationException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.GatewayException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.ThreeDsAuthenticationFailedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionAlreadyAuthorizedException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionMismatchException;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.TransactionNotFoundException;

/**
 * Activity stubs whose retry policy is a deliberate decision about money rather than a default.
 *
 * <p>Each stub here answers one question: how long is it worth retrying this call, and what does
 * the customer or the business lose while we do? The answers differ enough that sharing a single
 * stub would be wrong for all of them.
 *
 * <p>All timings come from {@link PaymentWorkflowTuning}, so they are read from the workflow
 * start memo and stay replay-safe.
 */
final class PaymentActivityStubs {

  private PaymentActivityStubs() {
  }

  /**
   * Stub for {@code settleTransaction} — a long-horizon durable retry.
   *
   * <p>By the time settlement runs, the guest has a confirmed booking and the money is
   * authorized. Giving up quickly means a stay nobody was charged for, so the retry horizon is
   * measured in days (72 hours by default) rather than attempts: each attempt gets ~30 seconds,
   * the interval grows exponentially from ~5 seconds up to a 15-minute cap, and the activity's
   * ScheduleToClose bounds the whole thing. A gateway outage of several hours is survived
   * without a human, and the workflow is not left polling every five seconds for three days.
   *
   * <p>Retrying is safe because the settle converges: the adapter resolves a repeat settlement
   * of an already-settled transaction against the gateway's transaction status and reports
   * success for the same transaction, refno, and amount rather than capturing twice.
   *
   * <p>Failures that are decisions rather than accidents — a decline, a 3-D Secure failure, a
   * transaction the gateway does not know, or a confirmed transaction that does not match this
   * payment — must short-circuit rather than burn the full horizon. Retrying them cannot change
   * the answer, and holding a doomed settlement open for three days delays the manual fix by
   * three days. Rejected merchant credentials join that list: retrying moves no money, but every
   * attempt carries the same wrong password.
   */
  static PaymentActivities settlementActivities() {
    return Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(PaymentWorkflowTuning.settlementAttemptTimeout())
            .setScheduleToCloseTimeout(PaymentWorkflowTuning.settlementHorizon())
            .setRetryOptions(RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(5))
                .setBackoffCoefficient(2.0)
                .setMaximumInterval(PaymentWorkflowTuning.settlementBackoffCap())
                .setDoNotRetry(
                    ThreeDsAuthenticationFailedException.class.getName(),
                    TransactionNotFoundException.class.getName(),
                    TransactionAlreadyAuthorizedException.class.getName(),
                    TransactionMismatchException.class.getName(),
                    DatatransAuthenticationException.class.getName())
                .build())
            .build());
  }

  /**
   * Stub for {@code publishAuthorisedPaymentEvent} — a short-horizon retry.
   *
   * <p>This publish is what triggers the booking downstream, and the customer is sitting on a
   * pending screen while it runs. Retrying for hours would leave them there; the horizon is
   * therefore three minutes by default, with a fast backoff (2 seconds growing to a 30-second
   * cap). Failing at that point is recoverable in a way settlement is not: no booking exists
   * yet, so the workflow cancels the authorization and lets the customer pay again.
   */
  static PaymentActivities publishActivities() {
    return Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setScheduleToCloseTimeout(PaymentWorkflowTuning.publishHorizon())
            .setRetryOptions(RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(2))
                .setBackoffCoefficient(2.0)
                .setMaximumInterval(Duration.ofSeconds(30))
                .build())
            .build());
  }

  /**
   * Stub for {@code validatePaymentMethods} — bounded retries that stop at a client error.
   *
   * <p>The Payment Method Entity Service answers a question about configuration, so a transient
   * failure is worth a couple of quick attempts. A {@code GatewayException} is not transient:
   * the adapter raises it only for a 4xx other than 429, and the request we would retry is byte
   * for byte the request that was just rejected. Retrying it holds the customer on the init
   * call for three round trips and writes the same ERROR three times before returning the
   * answer it already had.
   *
   * <p>429 and 5xx stay a {@code ServiceUnavailableException} and keep their retries — those
   * are the cases where waiting genuinely changes the outcome.
   */
  static PaymentActivities paymentMethodActivities() {
    return Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(RetryOptions.newBuilder()
                .setMaximumAttempts(3)
                .setDoNotRetry(GatewayException.class.getName())
                .build())
            .build());
  }

  /**
   * Stub for the pre-cancellation gateway status check.
   *
   * <p>Before cancelling a superseded or expired transaction, the workflow asks the gateway
   * whether it is even cancellable. Two of the possible answers are answers, not outages, and
   * must not be retried: a 404 means the gateway no longer knows the transaction (a
   * never-completed Secure Fields transaction is not even queryable) — which simply means there
   * is nothing to cancel — and rejected merchant credentials do not improve on a second attempt.
   * Transient failures get one quick retry; the check is best-effort either way.
   */
  static PaymentActivities cancellationCheckActivities() {
    return Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .setRetryOptions(RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setBackoffCoefficient(2.0)
                .setMaximumAttempts(2)
                .setDoNotRetry(
                    TransactionNotFoundException.class.getName(),
                    DatatransAuthenticationException.class.getName())
                .build())
            .build());
  }

  /**
   * Stub for the booking-completion basket-status poll.
   *
   * <p>A read with no side effects, called on a slow loop: a failed poll is not worth retrying
   * hard because the next tick of the loop is the retry. Two quick attempts, then the loop moves
   * on.
   */
  static PaymentActivities basketStatusActivities() {
    return Workflow.newActivityStub(
        PaymentActivities.class,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .setRetryOptions(RetryOptions.newBuilder()
                .setInitialInterval(Duration.ofSeconds(1))
                .setBackoffCoefficient(2.0)
                .setMaximumAttempts(2)
                .build())
            .build());
  }
}
