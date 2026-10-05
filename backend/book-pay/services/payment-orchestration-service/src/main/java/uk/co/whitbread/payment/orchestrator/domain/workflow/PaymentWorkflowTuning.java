package uk.co.whitbread.payment.orchestrator.domain.workflow;

import io.temporal.workflow.Workflow;
import java.time.Duration;

/**
 * Resolves the payment workflow's timing knobs from the start memo the adapter writes from
 * {@code integrations.payment.workflow.*}.
 *
 * <h2>Why a memo</h2>
 * <p>Workflow code cannot read Spring configuration: it must produce the same commands on every
 * replay, and a value pulled from a config server at replay time would not. A memo travels in
 * the workflow-start event and stays in the history, so reading one is deterministic. Histories
 * recorded before a knob existed simply return {@code null} and fall back to the default
 * constants here — which is why every default must stay at the value the knob shipped with.
 *
 * @see PaymentWorkflowImpl
 */
public final class PaymentWorkflowTuning {

  // --- Memo keys -----------------------------------------------------------

  /** Memo key for the pre-authorization expiry timeout. */
  public static final String EXPIRY_TIMEOUT_MEMO_KEY = "paymentWorkflowExpiryTimeoutMillis";

  /** Memo key for the overall settlement retry horizon (activity ScheduleToClose). */
  public static final String SETTLEMENT_HORIZON_MEMO_KEY =
      "paymentWorkflowSettlementRetryHorizonMillis";

  /** Memo key for the settlement retry backoff cap (maximum interval between attempts). */
  public static final String SETTLEMENT_BACKOFF_CAP_MEMO_KEY =
      "paymentWorkflowSettlementBackoffCapMillis";

  /** Memo key for the per-attempt settlement timeout (activity StartToClose). */
  public static final String SETTLEMENT_ATTEMPT_TIMEOUT_MEMO_KEY =
      "paymentWorkflowSettlementAttemptTimeoutMillis";

  /** Memo key for the overall {@code PaymentAuthorisedEvent} publish horizon. */
  public static final String PUBLISH_HORIZON_MEMO_KEY =
      "paymentWorkflowPublishRetryHorizonMillis";

  /** Memo key for the booking-completion poll interval. */
  public static final String BOOKING_POLL_INTERVAL_MEMO_KEY =
      "paymentWorkflowBookingPollIntervalMillis";

  /** Memo key for the overall booking-completion polling horizon. */
  public static final String BOOKING_POLL_HORIZON_MEMO_KEY =
      "paymentWorkflowBookingPollHorizonMillis";

  // --- Defaults (must match the shipped application.yml values) ------------

  /** Fallback pre-authorization expiry timeout. */
  public static final Duration DEFAULT_EXPIRY_TIMEOUT = Duration.ofMinutes(30);

  /** Fallback settlement retry horizon: three days of durable retrying. */
  public static final Duration DEFAULT_SETTLEMENT_HORIZON = Duration.ofHours(72);

  /** Fallback settlement backoff cap. */
  public static final Duration DEFAULT_SETTLEMENT_BACKOFF_CAP = Duration.ofMinutes(15);

  /** Fallback per-attempt settlement timeout. */
  public static final Duration DEFAULT_SETTLEMENT_ATTEMPT_TIMEOUT = Duration.ofSeconds(30);

  /** Fallback publish horizon — the customer is on a pending screen while this runs. */
  public static final Duration DEFAULT_PUBLISH_HORIZON = Duration.ofMinutes(3);

  /** Fallback booking-completion poll interval. */
  public static final Duration DEFAULT_BOOKING_POLL_INTERVAL = Duration.ofSeconds(60);

  /** Fallback booking-completion polling horizon. */
  public static final Duration DEFAULT_BOOKING_POLL_HORIZON = Duration.ofMinutes(45);

  private PaymentWorkflowTuning() {
  }

  /** Resolves the pre-authorization expiry timeout. */
  public static Duration expiryTimeout() {
    return resolve(EXPIRY_TIMEOUT_MEMO_KEY, DEFAULT_EXPIRY_TIMEOUT);
  }

  /** Resolves the overall settlement retry horizon. */
  public static Duration settlementHorizon() {
    return resolve(SETTLEMENT_HORIZON_MEMO_KEY, DEFAULT_SETTLEMENT_HORIZON);
  }

  /** Resolves the settlement retry backoff cap. */
  public static Duration settlementBackoffCap() {
    return resolve(SETTLEMENT_BACKOFF_CAP_MEMO_KEY, DEFAULT_SETTLEMENT_BACKOFF_CAP);
  }

  /** Resolves the per-attempt settlement timeout. */
  public static Duration settlementAttemptTimeout() {
    return resolve(SETTLEMENT_ATTEMPT_TIMEOUT_MEMO_KEY, DEFAULT_SETTLEMENT_ATTEMPT_TIMEOUT);
  }

  /** Resolves the overall {@code PaymentAuthorisedEvent} publish horizon. */
  public static Duration publishHorizon() {
    return resolve(PUBLISH_HORIZON_MEMO_KEY, DEFAULT_PUBLISH_HORIZON);
  }

  /** Resolves the booking-completion poll interval. */
  public static Duration bookingPollInterval() {
    return resolve(BOOKING_POLL_INTERVAL_MEMO_KEY, DEFAULT_BOOKING_POLL_INTERVAL);
  }

  /** Resolves the overall booking-completion polling horizon. */
  public static Duration bookingPollHorizon() {
    return resolve(BOOKING_POLL_HORIZON_MEMO_KEY, DEFAULT_BOOKING_POLL_HORIZON);
  }

  /**
   * Reads a millisecond value from the start memo, falling back to the given default when the
   * memo is absent (an older history, or a client that did not set it) or non-positive.
   */
  private static Duration resolve(String memoKey, Duration fallback) {
    Long configuredMillis = Workflow.getMemo(memoKey, Long.class, Long.class);
    if (configuredMillis == null || configuredMillis <= 0) {
      return fallback;
    }
    return Duration.ofMillis(configuredMillis);
  }
}
