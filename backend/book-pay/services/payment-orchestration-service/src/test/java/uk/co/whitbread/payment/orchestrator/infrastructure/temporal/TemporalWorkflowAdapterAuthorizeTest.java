package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.UpdateOptions;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowNotFoundException;
import io.temporal.client.WorkflowStub;
import io.temporal.client.WorkflowUpdateHandle;
import io.temporal.client.WorkflowUpdateStage;
import io.temporal.client.WorkflowUpdateTimeoutOrCancelledException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.payment.orchestrator.domain.model.AuthorizeResult;
import uk.co.whitbread.payment.orchestrator.domain.model.DatatransWebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.PaymentErrorCode;
import uk.co.whitbread.payment.orchestrator.domain.model.WebhookPayload;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatus;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentStatusResponse;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentWorkflow;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransReconciliationProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.DatatransWebhookProperties;
import uk.co.whitbread.payment.orchestrator.infrastructure.config.PaymentWorkflowProperties;

@ExtendWith(MockitoExtension.class)
class TemporalWorkflowAdapterAuthorizeTest {

  @Mock
  private WorkflowClient workflowClient;

  @Mock
  private DatatransWebhookProperties webhookProperties;

  @Mock
  private DatatransReconciliationProperties reconciliationProperties;

  private PaymentWorkflowProperties workflowProperties;

  private TemporalWorkflowAdapter underTest;

  private static final String BASKET_ID = "basket-123";
  private static final String TASK_QUEUE = "payment-workflows";
  private static final String WORKFLOW_ID = "payment-" + BASKET_ID;

  @BeforeEach
  void setUp() {
    workflowProperties = new PaymentWorkflowProperties();
    underTest = new TemporalWorkflowAdapter(
        workflowClient, webhookProperties, reconciliationProperties,
        workflowProperties, TASK_QUEUE);
  }

  /**
   * Wires the untyped-stub path the adapter uses for authorize, returning the update handle so
   * the test can decide what the bounded wait sees.
   */
  @SuppressWarnings("unchecked")
  private WorkflowUpdateHandle<AuthorizeResult> stubStartedUpdate() {
    WorkflowStub untypedStub = mock(WorkflowStub.class);
    WorkflowUpdateHandle<AuthorizeResult> handle = mock(WorkflowUpdateHandle.class);
    when(workflowClient.newUntypedWorkflowStub(eq(WORKFLOW_ID))).thenReturn(untypedStub);
    when(untypedStub.<AuthorizeResult>startUpdate(any(UpdateOptions.class))).thenReturn(handle);
    return handle;
  }

  @Nested
  class AuthorizePaymentSuccess {

    @Test
    void returnsSuccessfulAuthorizeResultFromUpdateMethod() {
      WorkflowUpdateHandle<AuthorizeResult> handle = stubStartedUpdate();
      when(handle.getResult(anyLong(), any(TimeUnit.class)))
          .thenReturn(new AuthorizeResult(true, null, null));

      AuthorizeResult result = underTest.authorizePayment(BASKET_ID);

      assertThat(result.success()).isTrue();
      assertThat(result.errorCode()).isNull();
      assertThat(result.errorMessage()).isNull();
    }

    @Test
    void startsTheAuthorizeUpdateWaitingOnlyForAcceptance() {
      WorkflowStub untypedStub = mock(WorkflowStub.class);
      WorkflowUpdateHandle<AuthorizeResult> handle = mockHandle();
      when(workflowClient.newUntypedWorkflowStub(eq(WORKFLOW_ID))).thenReturn(untypedStub);
      when(untypedStub.<AuthorizeResult>startUpdate(any(UpdateOptions.class))).thenReturn(handle);
      when(handle.getResult(anyLong(), any(TimeUnit.class)))
          .thenReturn(new AuthorizeResult(true, null, null));
      workflowProperties.setAuthorizeWait(Duration.ofSeconds(7));

      underTest.authorizePayment(BASKET_ID);

      ArgumentCaptor<UpdateOptions<AuthorizeResult>> options = optionsCaptor();
      verify(untypedStub).startUpdate(options.capture());
      // The name must match the unnamed @UpdateMethod on PaymentWorkflow.authorize(), which
      // Temporal registers under the bare method name — an untyped stub cannot derive it.
      assertThat(options.getValue().getUpdateName()).isEqualTo("authorize");
      // ACCEPTED, not COMPLETED: the update must be durably admitted before the bounded wait
      // starts, so a wait that elapses leaves a committed authorization running.
      assertThat(options.getValue().getWaitForStage()).isEqualTo(WorkflowUpdateStage.ACCEPTED);
      verify(handle).getResult(Duration.ofSeconds(7).toMillis(), TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    private WorkflowUpdateHandle<AuthorizeResult> mockHandle() {
      return mock(WorkflowUpdateHandle.class);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ArgumentCaptor<UpdateOptions<AuthorizeResult>> optionsCaptor() {
      return (ArgumentCaptor) ArgumentCaptor.forClass(UpdateOptions.class);
    }
  }

  @Nested
  class AuthorizeStillRunning {

    @Test
    void returnsAuthorizationPendingWhenBoundedWaitElapses() {
      WorkflowUpdateHandle<AuthorizeResult> handle = stubStartedUpdate();
      when(handle.getResult(anyLong(), any(TimeUnit.class)))
          .thenThrow(new WorkflowUpdateTimeoutOrCancelledException(
              WorkflowExecution.newBuilder().setWorkflowId(WORKFLOW_ID).build(),
              "update-1", "authorize", null));

      AuthorizeResult result = underTest.authorizePayment(BASKET_ID);

      // The authorization is committed and still running — telling the caller GATEWAY_ERROR
      // here would report a failure for a payment that may still charge the customer.
      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.AUTHORIZATION_PENDING);
      assertThat(result.errorMessage())
          .isEqualTo(PaymentErrorCode.AUTHORIZATION_PENDING.getErrorMessage());
    }

    @Test
    void doesNotThrowWhenBoundedWaitElapses() {
      WorkflowUpdateHandle<AuthorizeResult> handle = stubStartedUpdate();
      when(handle.getResult(anyLong(), any(TimeUnit.class)))
          .thenThrow(new WorkflowUpdateTimeoutOrCancelledException(
              WorkflowExecution.newBuilder().setWorkflowId(WORKFLOW_ID).build(),
              "update-1", "authorize", null));

      assertThatCode(() -> underTest.authorizePayment(BASKET_ID)).doesNotThrowAnyException();
    }
  }

  @Nested
  class NoWorkflowFound {

    @Test
    void returnsTransactionNotFoundWhenWorkflowNotFound() {
      WorkflowStub untypedStub = mock(WorkflowStub.class);
      when(workflowClient.newUntypedWorkflowStub(eq(WORKFLOW_ID))).thenReturn(untypedStub);
      when(untypedStub.<AuthorizeResult>startUpdate(any(UpdateOptions.class)))
          .thenThrow(new WorkflowNotFoundException(
              WorkflowExecution.newBuilder().setWorkflowId(WORKFLOW_ID).build(),
              "PaymentWorkflow", null));

      AuthorizeResult result = underTest.authorizePayment(BASKET_ID);

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.TRANSACTION_NOT_FOUND);
      assertThat(result.errorMessage()).contains(BASKET_ID);
    }
  }

  @Nested
  class WorkflowReturnsError {

    @Test
    void returnsErrorResultWhenWorkflowRejectsAuthorization() {
      WorkflowUpdateHandle<AuthorizeResult> handle = stubStartedUpdate();
      when(handle.getResult(anyLong(), any(TimeUnit.class)))
          .thenReturn(new AuthorizeResult(false, PaymentErrorCode.INVALID_TRANSACTION_STATE,
              "Payment not initialized"));

      AuthorizeResult result = underTest.authorizePayment(BASKET_ID);

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.INVALID_TRANSACTION_STATE);
      assertThat(result.errorMessage()).isEqualTo("Payment not initialized");
    }

    @Test
    void returnsAuthorizationInProgressWhenAlreadyAuthorizing() {
      WorkflowUpdateHandle<AuthorizeResult> handle = stubStartedUpdate();
      when(handle.getResult(anyLong(), any(TimeUnit.class)))
          .thenReturn(new AuthorizeResult(false, PaymentErrorCode.AUTHORIZATION_IN_PROGRESS,
              "Authorization already in progress for this payment"));

      AuthorizeResult result = underTest.authorizePayment(BASKET_ID);

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.AUTHORIZATION_IN_PROGRESS);
    }
  }

  @Nested
  class TemporalClientUnreachable {

    @Test
    void returnsGatewayErrorWhenClientThrowsRuntimeException() {
      when(workflowClient.newUntypedWorkflowStub(eq(WORKFLOW_ID)))
          .thenThrow(new RuntimeException("Connection refused"));

      AuthorizeResult result = underTest.authorizePayment(BASKET_ID);

      assertThat(result.success()).isFalse();
      assertThat(result.errorCode()).isEqualTo(PaymentErrorCode.GATEWAY_ERROR);
      // The exception text must stay in the log: the API message is constant so gRPC/SDK
      // internals (hosts, stack detail) never reach a caller.
      assertThat(result.errorMessage()).isEqualTo("Failed to communicate with payment workflow");
      assertThat(result.errorMessage()).doesNotContain("Connection refused");
    }
  }

  @Nested
  class PaymentStatusQuery {

    @Test
    void returnsStatusAndAuthorizeResultFromWorkflowQueries() {
      PaymentWorkflow workflowStub = mock(PaymentWorkflow.class);
      when(workflowClient.newWorkflowStub(eq(PaymentWorkflow.class), eq(WORKFLOW_ID)))
          .thenReturn(workflowStub);
      when(workflowStub.getPaymentStatus()).thenReturn(PaymentStatus.AUTHORIZED);
      when(workflowStub.getAuthorizeResult()).thenReturn(new AuthorizeResult(true, null, null));

      PaymentStatusResponse result = underTest.getPaymentStatus(BASKET_ID);

      assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
      assertThat(result.authorizeResult().success()).isTrue();
    }

    @Test
    void returnsNullAuthorizeResultWhileAuthorizationStillRunning() {
      PaymentWorkflow workflowStub = mock(PaymentWorkflow.class);
      when(workflowClient.newWorkflowStub(eq(PaymentWorkflow.class), eq(WORKFLOW_ID)))
          .thenReturn(workflowStub);
      when(workflowStub.getPaymentStatus()).thenReturn(PaymentStatus.INITIALIZED);
      when(workflowStub.getAuthorizeResult()).thenReturn(null);

      PaymentStatusResponse result = underTest.getPaymentStatus(BASKET_ID);

      assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.INITIALIZED);
      assertThat(result.authorizeResult()).isNull();
    }

    @Test
    void throwsBasketNotFoundWhenNoWorkflowExists() {
      when(workflowClient.newWorkflowStub(eq(PaymentWorkflow.class), eq(WORKFLOW_ID)))
          .thenThrow(new WorkflowNotFoundException(
              WorkflowExecution.newBuilder().setWorkflowId(WORKFLOW_ID).build(),
              "PaymentWorkflow", null));

      assertThatThrownBy(() -> underTest.getPaymentStatus(BASKET_ID))
          .isInstanceOf(BasketNotFoundException.class)
          .hasMessageContaining(BASKET_ID);
    }
  }

  @Nested
  class WebhookSignaling {

    @Test
    void signalWebhookReceived_deliversPayloadToWorkflow() {
      PaymentWorkflow workflowStub = mock(PaymentWorkflow.class);
      when(workflowClient.newWorkflowStub(eq(PaymentWorkflow.class), eq(WORKFLOW_ID)))
          .thenReturn(workflowStub);
      doNothing().when(workflowStub).webhookReceived(any(WebhookPayload.class));

      var payload = new DatatransWebhookPayload(
          "txn-456", "merchant-1", "authorized", "GBP", "PI-123",
          "VIS", 8600, "alias-123", "424242xxxxxx4242", "12", "25", "AUTH-789");

      underTest.signalWebhookReceived(BASKET_ID, payload);

      verify(workflowStub).webhookReceived(payload);
    }

    @Test
    void signalWebhookReceived_gracefullyHandlesMissingWorkflow() {
      PaymentWorkflow workflowStub = mock(PaymentWorkflow.class);
      when(workflowClient.newWorkflowStub(eq(PaymentWorkflow.class), eq(WORKFLOW_ID)))
          .thenReturn(workflowStub);
      doThrow(new WorkflowNotFoundException(
          WorkflowExecution.newBuilder().setWorkflowId(WORKFLOW_ID).build(),
          "PaymentWorkflow", null))
          .when(workflowStub).webhookReceived(any());

      var payload = new DatatransWebhookPayload(
          "txn-456", "merchant-1", "authorized", "GBP", "PI-123",
          "VIS", 8600, null, null, null, null, null);

      // Should not throw — the adapter logs and swallows the exception
      assertThatCode(() -> underTest.signalWebhookReceived(BASKET_ID, payload))
          .doesNotThrowAnyException();
    }
  }
}
