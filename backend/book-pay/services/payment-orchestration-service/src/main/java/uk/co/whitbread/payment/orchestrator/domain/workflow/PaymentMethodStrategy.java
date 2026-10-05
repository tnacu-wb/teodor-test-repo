package uk.co.whitbread.payment.orchestrator.domain.workflow;

import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitCommand;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentInitResult;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Strategy interface for payment-method-specific initialization and authorization logic.
 *
 * <p>Each implementation encapsulates the behaviour unique to a single Datatrans integration
 * type (e.g. Secure Fields for web, Mobile SDK for native). The unified
 * {@code PaymentWorkflowImpl} delegates to the appropriate strategy based on the
 * {@link PaymentMethod} specified in the initialization command.
 *
 * <h2>Event-Inbox Consumption Pattern</h2>
 *
 * <p>Strategies do <strong>not</strong> receive events directly from signal handlers.
 * Instead, signal handlers on the workflow validate incoming events and deposit them into
 * {@link WorkflowState} inbox fields (e.g. {@code authorizeSignalReceived},
 * {@code webhookPayload}, {@code bookingCompletedEvent}). Strategies then consume these
 * events by calling {@code Workflow.await(predicate)} with a predicate that checks the
 * relevant inbox field on {@link WorkflowState}.
 *
 * <p>This decoupling provides several benefits:
 * <ul>
 *   <li>Signal handlers remain generic — they validate and deposit without routing to a
 *       specific strategy.</li>
 *   <li>Strategies are deterministic — they only read validated state, ensuring safe
 *       Temporal workflow replay.</li>
 *   <li>Future payment methods can consume any combination of inbox fields without
 *       requiring signal handler changes.</li>
 * </ul>
 *
 * <h2>Transaction ID Validation</h2>
 *
 * <p>Signal handlers validate {@code payload.transactionId()} against
 * {@code state.getTransactionId()} <em>before</em> depositing into the inbox. Strategies
 * therefore only consume pre-validated events and do not need to perform their own
 * transaction ID checks (Requirement 7.5).
 *
 * <h2>Implementation Constraints</h2>
 *
 * <p>Implementations must be <strong>plain deterministic Java classes</strong> with no
 * Spring dependencies or framework annotations. All mutable state is managed externally
 * in {@link WorkflowState}, making strategies stateless and safe for Temporal replay.
 *
 * @see WorkflowState
 * @see PaymentActivities
 * @see PaymentInitCommand
 */
public interface PaymentMethodStrategy {

  /**
   * Initialize a payment transaction for the given command.
   *
   * <p>Performs method-specific initialization by calling the appropriate
   * {@link PaymentActivities} methods and updating {@link WorkflowState} with the
   * resulting transaction details (transaction ID, merchant ID, amounts, etc.).
   *
   * @param command    the payment initialization command containing method-specific
   *                   parameters (e.g. returnUrl for web, no returnUrl for mobile)
   * @param activities the Temporal activity stub for external service calls
   * @param state      the mutable workflow state to update with initialization results
   * @return the initialization result indicating success or failure with typed error code
   */
  PaymentInitResult init(PaymentInitCommand command, PaymentActivities activities,
      WorkflowState state);

  /**
   * Await and process authorization for the current transaction.
   *
   * <p>Blocks using {@code Workflow.await(predicate)} until the strategy-specific
   * authorization event arrives in the {@link WorkflowState} inbox. For example:
   * <ul>
   *   <li>{@code NewCardWebStrategy} awaits
   *       {@code state.isAuthorizeSignalReceived()} — the frontend calls the authorize
   *       update after collecting card details via Secure Fields.</li>
   *   <li>{@code NewCardMobileStrategy} awaits
   *       {@code state.getWebhookPayload() != null} — Datatrans posts a webhook after
   *       the native SDK completes 3-D Secure.</li>
   * </ul>
   *
   * <p>Once the inbox event is consumed, the strategy calls the appropriate
   * {@link PaymentActivities} methods to complete authorization and updates
   * {@link WorkflowState} with the outcome (authorized amount, card alias, etc.).
   *
   * @param activities the Temporal activity stub for external service calls
   * @param state      the mutable workflow state containing inbox fields and
   *                   transaction context
   */
  void awaitAuthorization(PaymentActivities activities, WorkflowState state);

  /**
   * Returns the {@link PaymentMethod} integration type this strategy handles.
   *
   * <p>Used by {@code PaymentMethodStrategyFactory} to select the correct strategy
   * for a given payment initialization command. Each strategy handles exactly one
   * integration type.
   *
   * @return the supported payment method enum value
   */
  PaymentMethod getSupportedMethod();
}
