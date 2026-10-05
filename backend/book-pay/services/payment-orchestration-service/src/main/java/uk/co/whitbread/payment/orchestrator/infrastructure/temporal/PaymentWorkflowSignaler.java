package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowNotFoundException;
import io.temporal.client.WorkflowStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;

/**
 * Infrastructure component that bridges Kafka event consumption and Temporal workflow signaling.
 *
 * <p>Sends a {@code bookingCompleted} signal to the running payment workflow
 * ({@code PaymentWorkflow}) identified by
 * the deterministic workflow ID {@code payment-{basketId}}.
 *
 * <p>If the workflow is not found (already completed, timed out, or never started), a warning
 * is logged and no exception is propagated — this is expected in scenarios where the payment
 * workflow has already reached a terminal state.
 *
 * <p>Excluded from the {@code integration} profile where no Temporal server is available.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!integration")
public class PaymentWorkflowSignaler {

  private static final String WORKFLOW_ID_PREFIX = "payment-";
  private static final String SIGNAL_NAME = "bookingCompleted";

  private final WorkflowClient workflowClient;

  /**
   * Signals the payment workflow with a {@link BookingCompletedEvent}.
   *
   * <p>Uses an untyped workflow stub to send the signal because the same workflow ID pattern
   * is shared by both Secure Fields and Mobile SDK workflows. The signal name
   * ({@code bookingCompleted}) is identical on both workflow interfaces.
   *
   * @param basketId the basket identifier used to derive the workflow ID
   * @param event    the booking completed event containing status (COMPLETED or FAILED)
   */
  public void signalBookingCompleted(String basketId, BookingCompletedEvent event) {
    String workflowId = WORKFLOW_ID_PREFIX + basketId;
    log.info("Signaling BookingCompletedEvent to workflow [workflowId={}, basketReference={}, status={}]",
        workflowId, event.basketReference(), event.status());

    try {
      WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
      workflowStub.signal(SIGNAL_NAME, event);
      log.info("BookingCompletedEvent signal delivered [workflowId={}, status={}]",
          workflowId, event.status());
    } catch (WorkflowNotFoundException e) {
      log.warn("No open payment workflow found to signal [workflowId={}, basketReference={}]. "
          + "The workflow may have already completed or timed out.", workflowId, event.basketReference());
    }
  }
}
