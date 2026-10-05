package uk.co.whitbread.payment.orchestrator.domain.workflow;

import java.util.Objects;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Factory for creating payment method strategies based on the {@link PaymentMethod} integration
 * type.
 *
 * <h2>Temporal Workflow Determinism</h2>
 *
 * <p>This factory is invoked inside Temporal workflow code and <strong>must remain
 * deterministic</strong>. The same {@link PaymentMethod} input must always produce the same
 * strategy type. This guarantee is critical for safe workflow replay — Temporal re-executes
 * workflow code from the beginning on each replay, and non-deterministic factories would cause
 * replay failures (non-determinism errors).
 *
 * <h2>Design Decisions</h2>
 *
 * <ul>
 *   <li><strong>Static utility class</strong> — no mutable state, no registration, no Spring
 *       dependencies. The factory is a pure function from enum to strategy instance.</li>
 *   <li><strong>Exhaustive switch expression, no default</strong> — a {@code default} branch
 *       would silence the compiler's exhaustiveness check, so there deliberately is none:
 *       adding a {@link PaymentMethod} constant fails compilation here until a strategy case
 *       exists. The JVM guards the residual runtime gap (an enum constant from a newer class
 *       version than this switch was compiled against) on its own.</li>
 *   <li><strong>New instance per call</strong> — strategies are stateless (all mutable state
 *       lives in {@link WorkflowState}), so fresh instances are safe and avoid shared-state
 *       bugs across workflow replays.</li>
 * </ul>
 *
 * <h2>Adding a New Payment Method</h2>
 *
 * <p>To add a new integration type (e.g. Google Pay, Apple Pay, Saved Card):
 * <ol>
 *   <li>Add the new constant to {@link PaymentMethod}.</li>
 *   <li>Create a corresponding {@link PaymentMethodStrategy} implementation in this
 *       package.</li>
 *   <li>Add the new case to the switch expression in {@link #create(PaymentMethod)}.</li>
 *   <li>Add replay tests to verify backward compatibility with existing workflow
 *       histories.</li>
 * </ol>
 *
 * @see PaymentMethodStrategy
 * @see PaymentMethod
 * @see WorkflowState
 */
public final class PaymentMethodStrategyFactory {

  private PaymentMethodStrategyFactory() {
    // Utility class — prevent instantiation
  }

  /**
   * Creates the appropriate {@link PaymentMethodStrategy} for the given integration type.
   *
   * <p>This method is deterministic and safe for Temporal workflow replay. It contains no
   * I/O, no randomness, and no time-dependent logic.
   *
   * @param method the Datatrans integration type to create a strategy for
   * @return a new strategy instance handling the specified payment method
   * @throws NullPointerException if {@code method} is null (a workflow started without a
   *     current method — a programming error, not a customer input)
   */
  public static PaymentMethodStrategy create(PaymentMethod method) {
    Objects.requireNonNull(method, "paymentMethod must not be null");
    return switch (method) {
      case NEW_CARD_WEB -> new NewCardWebStrategy();
      case NEW_CARD_MOBILE -> new NewCardMobileStrategy();
    };
  }
}
