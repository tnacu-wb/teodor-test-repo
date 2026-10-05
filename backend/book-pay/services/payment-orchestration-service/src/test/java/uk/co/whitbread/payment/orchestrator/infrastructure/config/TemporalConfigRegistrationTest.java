package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.temporal.client.WorkflowClient;
import io.temporal.opentracing.OpenTracingOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.context.SmartLifecycle;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentActivities;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentWorkflowImpl;
import uk.co.whitbread.payment.orchestrator.infrastructure.temporal.PaymentActivitiesImpl;

class TemporalConfigRegistrationTest {

  private static final String TASK_QUEUE = "payment-workflows";

  @Test
  void registersPaymentActivitiesIncludingTransactionStatusOnPaymentWorkflows() throws Exception {
    WorkflowClient workflowClient = mock(WorkflowClient.class);
    PaymentActivitiesImpl paymentActivities = mock(PaymentActivitiesImpl.class);
    OpenTracingOptions tracingOptions = mock(OpenTracingOptions.class);
    WorkerFactory workerFactory = mock(WorkerFactory.class);
    Worker worker = mock(Worker.class);
    when(workerFactory.newWorker(TASK_QUEUE)).thenReturn(worker);

    TemporalConfig underTest = new TemporalConfig();
    ReflectionTestUtils.setField(underTest, "taskQueue", TASK_QUEUE);

    try (MockedStatic<WorkerFactory> mockedWorkerFactory =
        org.mockito.Mockito.mockStatic(WorkerFactory.class)) {
      mockedWorkerFactory.when(() -> WorkerFactory.newInstance(
          eq(workflowClient), any(WorkerFactoryOptions.class))).thenReturn(workerFactory);

      WorkerFactory result = underTest.workerFactory(workflowClient, paymentActivities, tracingOptions);

      assertThat(result).isSameAs(workerFactory);
      verify(workerFactory).newWorker(TASK_QUEUE);
      verify(worker).registerWorkflowImplementationTypes(
          PaymentWorkflowImpl.class);
      verify(worker).registerActivitiesImplementations(paymentActivities);
      // Registration must not start polling: the worker would accept activity tasks while the
      // context is still refreshing. Starting is the lifecycle bean's job.
      verify(workerFactory, never()).start();
    }

    Method statusActivity = PaymentActivities.class.getMethod(
        "getTransactionStatus", String.class, String.class);
    assertThat(statusActivity.getDeclaringClass()).isEqualTo(PaymentActivities.class);
    assertThat(PaymentActivities.class.isAssignableFrom(PaymentActivitiesImpl.class)).isTrue();
  }

  @Test
  void lifecycleStartsPollingOnRefreshAndShutsTheFactoryDownOnStop() {
    WorkerFactory workerFactory = mock(WorkerFactory.class);
    TemporalConfig underTest = new TemporalConfig();

    SmartLifecycle lifecycle = underTest.workerFactoryLifecycle(workerFactory);

    assertThat(lifecycle.isRunning()).isFalse();
    // Runs last on refresh so the worker only polls once every other bean is ready.
    assertThat(lifecycle.getPhase()).isEqualTo(Integer.MAX_VALUE);

    lifecycle.start();
    verify(workerFactory).start();
    assertThat(lifecycle.isRunning()).isTrue();

    lifecycle.stop();
    verify(workerFactory).shutdown();
    assertThat(lifecycle.isRunning()).isFalse();
  }
}
