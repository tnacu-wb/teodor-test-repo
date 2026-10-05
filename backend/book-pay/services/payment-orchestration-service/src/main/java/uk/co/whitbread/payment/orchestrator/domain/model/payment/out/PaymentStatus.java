package uk.co.whitbread.payment.orchestrator.domain.model.payment.out;

/**
 * Lifecycle states of a payment session.
 *
 * <p>A payment progresses from {@code INITIALIZED} through authorization, then through the
 * booking and settlement phases. Two predicates cut that lifecycle at two different points and
 * must not be confused: {@link #authorizationPhaseComplete()} says the gateway has answered on
 * the authorization, {@link #isFinal()} says the workflow can close.
 */
public enum PaymentStatus {
  INITIALIZED,
  AUTHORIZED,

  /**
   * Settlement has been scheduled and is being retried against the gateway.
   *
   * <p>Entered the moment the workflow decides to capture the money — the booking is confirmed
   * (either through the {@code bookingCompleted} signal or through the basket-status poll) and
   * the settle activity has been scheduled. It exists so a query can tell "the capture is being
   * retried" apart from "we are still waiting for the booking to complete": both used to look
   * like {@code AUTHORIZED}, and only one of them needs an operator's attention if it lasts.
   *
   * <p>Not terminal — the settle activity's long retry horizon resolves it to {@link #SETTLED}
   * or {@link #SETTLEMENT_FAILED}. Re-initialization is rejected while in this state: the funds
   * are held and the booking exists, so a second payment attempt would double-charge.
   */
  SETTLEMENT_PENDING,
  SETTLED,

  /**
   * Terminal: the booking exists and the money is authorized, but the capture could not be
   * completed and needs a human.
   *
   * <p>Entered when the settle activity exhausts its retry horizon (72 hours by default) or
   * fails non-retryably (a mismatch, an unknown transaction, rejected merchant credentials).
   * The authorization is deliberately <strong>never</strong> cancelled here: the guest has a
   * confirmed booking, and voiding the hold would hand out a free stay. The workflow logs at
   * {@code ERROR} with the transaction id, booking reference, amount, and merchant id so the
   * settlement can be completed by hand in the Datatrans dashboard.
   *
   * <p>Re-initialization is rejected — unlike {@link #FAILED}, this is not a payment the
   * customer can retry; the money is already held.
   *
   * <p>A manual settlement in the dashboard is not lost work if the workflow ever settles again:
   * {@code settleTransaction} converges, resolving a repeat settle of an already-settled
   * transaction against the gateway's transaction status and reporting success rather than
   * capturing twice, so any later attempt lands on {@link #SETTLED}.
   */
  SETTLEMENT_FAILED,

  /**
   * Terminal: the payment is authorized and the booking outcome never arrived.
   *
   * <p>Entered when the booking-completion polling horizon is exhausted while the basket is
   * still in a pending state — neither completed nor failed. That is genuinely ambiguous: the
   * booking may yet complete, so the authorization is <strong>not</strong> cancelled (cancelling
   * a booking that later completes gives away a free stay) and settlement is not forced
   * (capturing for a booking that never happens charges for nothing). The workflow parks here
   * and logs at {@code ERROR} for an operator to reconcile.
   *
   * <p>Distinct from {@link #SETTLEMENT_FAILED}, which means "authorized, booked, and the
   * capture failed" — a different remediation entirely. Re-initialization is rejected: the funds
   * are held.
   */
  BOOKING_PENDING_TIMEOUT,
  FAILED,
  CANCELLED,
  EXPIRED;

  /**
   * Returns {@code true} once the authorization phase is over — the gateway has given its answer,
   * whatever that answer is, and nothing is left to wait for on the authorization.
   *
   * <p>This is the predicate the await loops use: the workflow's initialization wait, and the
   * mobile strategy's webhook and reconciliation waits. It does <strong>not</strong> mean the
   * payment or the workflow is done — see {@link #isFinal()}. {@code AUTHORIZED} answers
   * {@code true} here (the authorization succeeded, so there is nothing more to await) while the
   * booking and settlement phases are still ahead of it, and {@code FAILED} answers {@code true}
   * while the customer may still re-initialize.
   *
   * <p>{@code SETTLEMENT_PENDING} answers {@code false}: it is only ever reached after the
   * authorization phase has already ended, so no await loop ever asks.
   *
   * @return {@code true} if the authorization phase has produced its outcome
   */
  public boolean authorizationPhaseComplete() {
    return switch (this) {
      case AUTHORIZED, SETTLED, SETTLEMENT_FAILED, BOOKING_PENDING_TIMEOUT,
           FAILED, CANCELLED, EXPIRED -> true;
      case INITIALIZED, SETTLEMENT_PENDING -> false;
    };
  }

  /**
   * Returns {@code true} once the workflow has nothing left to do and can close.
   *
   * <p>{@code FAILED} is absent on purpose: it permits re-initialization, so the workflow must
   * stay open for another attempt inside the expiry window. {@code SETTLEMENT_PENDING} is absent
   * because the settle retry horizon has not resolved yet.
   *
   * <p>Final for the workflow is not the same as resolved for the business:
   * {@code SETTLEMENT_FAILED} and {@code BOOKING_PENDING_TIMEOUT} both close the workflow with
   * the customer's money still held and an operator still owed a manual reconciliation — see
   * {@link #holdsFunds()}.
   *
   * @return {@code true} if the workflow should stop here
   */
  public boolean isFinal() {
    return switch (this) {
      case SETTLED, CANCELLED, EXPIRED, SETTLEMENT_FAILED, BOOKING_PENDING_TIMEOUT -> true;
      case INITIALIZED, AUTHORIZED, SETTLEMENT_PENDING, FAILED -> false;
    };
  }

  /**
   * Returns {@code true} if this status holds customer funds that the workflow must not release
   * and must not re-attempt payment for.
   *
   * <p>These states reject re-initialization for a reason {@link #FAILED} does not share: an
   * authorization is outstanding, so a second attempt would take the money twice.
   *
   * @return {@code true} if re-initialization must be rejected because funds are held
   */
  public boolean holdsFunds() {
    return this == SETTLEMENT_PENDING
        || this == SETTLEMENT_FAILED
        || this == BOOKING_PENDING_TIMEOUT;
  }
}
