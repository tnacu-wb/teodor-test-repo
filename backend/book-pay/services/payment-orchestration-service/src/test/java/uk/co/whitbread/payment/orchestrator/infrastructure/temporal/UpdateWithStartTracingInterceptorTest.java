package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.opentracingshim.OpenTracingShim;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.update.v1.WaitPolicy;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.interceptors.Header;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptor;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptor.StartUpdateInput;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptor.WorkflowStartInput;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptor.WorkflowUpdateWithStartInput;
import io.temporal.opentracing.OpenTracingOptions;
import io.temporal.opentracing.internal.ContextAccessor;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Covers the propagation gap this interceptor exists to close: without it an Update-With-Start
 * reaches the worker with no tracing header, so the workflow's activities call downstream
 * services with no baggage.
 */
class UpdateWithStartTracingInterceptorTest {

  private static final String TEST_ID_KEY = "wb-test-id";
  private static final String TEST_ID = "a-scenario-test-id";

  private final OpenTelemetry openTelemetry = OpenTelemetry.propagating(
      ContextPropagators.create(W3CBaggagePropagator.getInstance()));
  private final OpenTracingOptions options = OpenTracingOptions.newBuilder()
      .setTracer(OpenTracingShim.createTracerShim(openTelemetry))
      .build();

  @Test
  void carriesCallerBaggageOntoBothHalvesOfUpdateWithStart() {
    WorkflowClientCallsInterceptor next = mock(WorkflowClientCallsInterceptor.class);
    var callsInterceptor = new UpdateWithStartTracingInterceptor(options)
        .workflowClientCallsInterceptor(next);

    Scope callerContext = Baggage.builder().put(TEST_ID_KEY, TEST_ID).build().makeCurrent();
    try {
      callsInterceptor.updateWithStart(anInput());
    } finally {
      callerContext.close();
    }

    ArgumentCaptor<WorkflowUpdateWithStartInput<String>> captor = ArgumentCaptor.captor();
    verify(next).updateWithStart(captor.capture());
    WorkflowUpdateWithStartInput<String> delivered = captor.getValue();

    assertThat(baggageOn(delivered.getStartUpdateInput().getHeader())).containsEntry(
        TEST_ID_KEY, TEST_ID);
    assertThat(baggageOn(delivered.getWorkflowStartInput().getHeader())).containsEntry(
        TEST_ID_KEY, TEST_ID);
  }

  @Test
  void leavesTheCallUntouchedWhenThereIsNoCallerContext() {
    WorkflowClientCallsInterceptor next = mock(WorkflowClientCallsInterceptor.class);
    var callsInterceptor = new UpdateWithStartTracingInterceptor(options)
        .workflowClientCallsInterceptor(next);

    WorkflowUpdateWithStartInput<String> input = anInput();
    callsInterceptor.updateWithStart(input);

    verify(next).updateWithStart(input);
  }

  @Test
  void isRegisteredWithoutDisturbingTheOtherClientCalls() {
    WorkflowClientCallsInterceptor next = mock(WorkflowClientCallsInterceptor.class);
    var callsInterceptor = new UpdateWithStartTracingInterceptor(options)
        .workflowClientCallsInterceptor(next);

    callsInterceptor.signal(mock(WorkflowClientCallsInterceptor.WorkflowSignalInput.class));

    verify(next).signal(any());
  }

  /** Reads the span context back out of the header exactly as the worker does. */
  private Map<String, String> baggageOn(Header header) {
    var spanContext = new ContextAccessor(options)
        .readSpanContextFromHeader(header, options.getTracer());
    assertThat(spanContext).isNotNull();

    return java.util.stream.StreamSupport.stream(spanContext.baggageItems().spliterator(), false)
        .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private WorkflowUpdateWithStartInput<String> anInput() {
    return new WorkflowUpdateWithStartInput<>(
        new WorkflowStartInput(
            "payment-AQN-basket",
            "PaymentWorkflow",
            Header.empty(),
            new Object[] {"AQN-basket"},
            WorkflowOptions.newBuilder().setTaskQueue("payment-workflows").build()),
        new StartUpdateInput<>(
            WorkflowExecution.newBuilder().setWorkflowId("payment-AQN-basket").build(),
            Optional.of("PaymentWorkflow"),
            "init",
            Header.empty(),
            "update-id",
            new Object[] {"https://example.test/return"},
            String.class,
            String.class,
            "",
            WaitPolicy.newBuilder().build()));
  }
}
