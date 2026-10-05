package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import jakarta.validation.Valid;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Externalised configuration for the payment workflow lifecycle.
 *
 * <p>Maps to the {@code integrations.payment.workflow} section in application.yml. Every value
 * here reaches the workflow as a start memo (see {@code PaymentWorkflowTuning}), because workflow
 * code must not read configuration directly — a value that can change between a run and its
 * replay breaks determinism.
 *
 * <h2>Timeout Behavior</h2>
 * <p>The {@code timeout} controls how long a payment workflow stays open <em>before
 * authorization</em> — the abandoned-basket case. After the timeout fires, the workflow applies
 * an authorization safety guard: it awaits {@code !authorizationInProgress} before transitioning
 * to EXPIRED. On expiry, any active Datatrans transaction is cancelled. The timer deliberately
 * does not apply after authorization, where cancelling could void a confirmed booking.
 *
 * <h2>Configuration Example</h2>
 * <pre>{@code
 * integrations:
 *   payment:
 *     workflow:
 *       timeout: 30m
 *       authorize-wait: 30s
 *       settlement:
 *         retry-horizon: 72h
 *         backoff-cap: 15m
 *         attempt-timeout: 30s
 *       publish:
 *         retry-horizon: 3m
 *       booking-poll:
 *         interval: 60s
 *         horizon: 45m
 * }</pre>
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "integrations.payment.workflow")
public class PaymentWorkflowProperties {

  /**
   * Maximum duration a payment workflow stays open before expiring an unauthorized payment.
   *
   * <p>Defaults to 30 minutes. After this timeout:
   * <ol>
   *   <li>The authorization safety guard is applied (await !authorizationInProgress)</li>
   *   <li>If the payment is authorized, the booking phase takes over and expiry does not fire</li>
   *   <li>Otherwise the workflow transitions to EXPIRED and cancels the Datatrans transaction</li>
   * </ol>
   */
  @PositiveDuration
  private Duration timeout = Duration.ofMinutes(30);

  /**
   * How long {@code POST /api/payments/authorize} waits for the workflow's authorize update
   * before answering "still processing".
   *
   * <p>Unlike every other value here this one never reaches the workflow — it bounds the
   * <em>client</em> side of the update call. The update itself is durably accepted before the
   * wait starts, so the workflow keeps running when the wait elapses; the caller is told to poll
   * the status endpoint rather than being handed a fabricated gateway error.
   *
   * <p>Defaults to 30 seconds, which must stay below the shortest idle timeout in front of this
   * service (load balancer, ingress, browser fetch). Raising it past that timeout puts the
   * honest 202 back out of reach — the proxy would cut the connection first.
   */
  @PositiveDuration
  private Duration authorizeWait = Duration.ofSeconds(30);

  /** Settlement retry policy. */
  @Valid
  private Settlement settlement = new Settlement();

  /** Authorised-event publish retry policy. */
  @Valid
  private Publish publish = new Publish();

  /** Booking-completion reconciliation polling. */
  @Valid
  private BookingPoll bookingPoll = new BookingPoll();

  /**
   * Retry policy for {@code settleTransaction}, the call that actually takes the guest's money.
   *
   * <p>By the time it runs, the booking is confirmed and the funds are authorized, so giving up
   * early means a stay nobody paid for. The horizon is therefore measured in days rather than in
   * attempts.
   */
  @Data
  public static class Settlement {

    /**
     * Total time settlement may keep retrying before the payment is parked as SETTLEMENT_FAILED.
     *
     * <p>Delivered as the settle activity's ScheduleToClose timeout. Three days survives a long
     * gateway outage without a human and still bounds the workflow.
     */
    @PositiveDuration
    private Duration retryHorizon = Duration.ofHours(72);

    /**
     * Upper bound on the exponential backoff between settlement attempts.
     *
     * <p>Without a cap, exponential growth over a 72-hour horizon would stretch the gap between
     * attempts into many hours and delay recovery long after the gateway came back.
     */
    @PositiveDuration
    private Duration backoffCap = Duration.ofMinutes(15);

    /** Per-attempt timeout for a single settle call (activity StartToClose). */
    @PositiveDuration
    private Duration attemptTimeout = Duration.ofSeconds(30);
  }

  /**
   * Retry policy for publishing {@code PaymentAuthorisedEvent}.
   *
   * <p>This publish triggers the booking, and the customer is waiting on a pending screen while
   * it runs — so the horizon is minutes, not days. On exhaustion the workflow cancels the
   * authorization and fails the attempt, which the customer can retry.
   */
  @Data
  public static class Publish {

    /**
     * Total time the authorised-event publish may keep retrying before the workflow compensates.
     *
     * <p>Defaults to 3 minutes: long enough to ride out a broker hiccup, short enough that a
     * waiting customer gets an answer.
     */
    @PositiveDuration
    private Duration retryHorizon = Duration.ofMinutes(3);
  }

  /**
   * Booking-completion reconciliation: how often to ask the Basket Service what happened, and for
   * how long, when the {@code bookingCompleted} Kafka event does not arrive.
   */
  @Data
  public static class BookingPoll {

    /**
     * How long to wait for the {@code bookingCompleted} signal before asking the Basket Service.
     *
     * <p>Defaults to 60 seconds. The signal normally arrives within seconds of the booking
     * completing, so a minute is comfortably past "it is just slow" while keeping the poll load
     * on the Basket Service to roughly one request per payment per minute.
     */
    @PositiveDuration
    private Duration interval = Duration.ofSeconds(60);

    /**
     * Total time to keep reconciling before parking the payment as BOOKING_PENDING_TIMEOUT.
     *
     * <p>Defaults to 45 minutes. A booking that has not resolved either way in that time is not
     * going to resolve without someone looking at it, and the payment should be visible to
     * operations rather than waiting quietly.
     */
    @PositiveDuration
    private Duration horizon = Duration.ofMinutes(45);
  }
}
