package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowNotFoundException;
import io.temporal.client.WorkflowStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.payment.orchestrator.domain.model.BookingCompletedEvent;

class PaymentWorkflowSignalerTest {

  private static final String BASKET_ID = "AQN-147756bb-bb71-4842-959a-2efe87e378ed";
  private static final String WORKFLOW_ID = "payment-" + BASKET_ID;

  private WorkflowClient workflowClient;
  private WorkflowStub workflowStub;
  private PaymentWorkflowSignaler underTest;

  @BeforeEach
  void setUp() {
    workflowClient = mock(WorkflowClient.class);
    workflowStub = mock(WorkflowStub.class);
    when(workflowClient.newUntypedWorkflowStub(WORKFLOW_ID)).thenReturn(workflowStub);
    underTest = new PaymentWorkflowSignaler(workflowClient);
  }

  @Test
  void signalsWorkflowWithBookingCompletedEvent() {
    var event = new BookingCompletedEvent(BASKET_ID, "COMPLETED");

    underTest.signalBookingCompleted(BASKET_ID, event);

    verify(workflowClient).newUntypedWorkflowStub(WORKFLOW_ID);
    verify(workflowStub).signal("bookingCompleted", event);
  }

  @Test
  void signalsWorkflowWithFailedBookingEvent() {
    var event = new BookingCompletedEvent(BASKET_ID, "FAILED");

    underTest.signalBookingCompleted(BASKET_ID, event);

    verify(workflowClient).newUntypedWorkflowStub(WORKFLOW_ID);
    verify(workflowStub).signal("bookingCompleted", event);
  }

  @Test
  void logsWarningAndDoesNotThrowWhenWorkflowNotFound() {
    var event = new BookingCompletedEvent(BASKET_ID, "COMPLETED");
    doThrow(new WorkflowNotFoundException(
        WorkflowExecution.getDefaultInstance(), "PaymentWorkflow", null))
        .when(workflowStub).signal("bookingCompleted", event);

    assertThatCode(() -> underTest.signalBookingCompleted(BASKET_ID, event))
        .doesNotThrowAnyException();
  }

  @Test
  void usesCorrectWorkflowIdPattern() {
    String customBasketId = "custom-basket-456";
    String expectedWorkflowId = "payment-" + customBasketId;
    WorkflowStub customStub = mock(WorkflowStub.class);
    when(workflowClient.newUntypedWorkflowStub(expectedWorkflowId)).thenReturn(customStub);
    var event = new BookingCompletedEvent(customBasketId, "COMPLETED");

    underTest.signalBookingCompleted(customBasketId, event);

    verify(workflowClient).newUntypedWorkflowStub(expectedWorkflowId);
    verify(customStub).signal("bookingCompleted", event);
  }
}
