package uk.co.whitbread.payment.orchestrator.infrastructure.temporal;

import io.opentracing.Span;
import io.opentracing.Tracer;
import io.temporal.common.interceptors.Header;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptor;
import io.temporal.common.interceptors.WorkflowClientCallsInterceptorBase;
import io.temporal.common.interceptors.WorkflowClientInterceptorBase;
import io.temporal.opentracing.OpenTracingOptions;
import io.temporal.opentracing.internal.ContextAccessor;
import java.util.HashMap;

/**
 * Carries the caller's trace and baggage across Temporal's Update-With-Start call.
 *
 * <p>{@code temporal-opentracing} overrides {@code start}, {@code signal},
 * {@code signalWithStart}, {@code query} and {@code startUpdate}, but not
 * {@code updateWithStart}. An Update-With-Start therefore reaches the worker with no tracing
 * header at all: the workflow and its activities begin under an empty context, and every
 * downstream call an activity makes loses the inbound W3C {@code traceparent} and
 * {@code baggage}. The worker side already handles the update path — its inbound interceptor
 * reads the header on {@code executeUpdate} — so only the client-side injection is missing.
 *
 * <p>Injection goes through the library's own {@link ContextAccessor} so the encoding stays
 * symmetrical with what the worker extracts. That is an internal package, which is why the
 * {@code temporal-opentracing} version is pinned in lockstep with {@code temporal-sdk}: an
 * upgrade must re-check both this class and whether the library has since implemented
 * {@code updateWithStart} itself, in which case this one can be deleted.
 *
 * <p>Registered only when {@code temporal.tracing.update-with-start.enabled} is true, which is
 * deliberately the integration stack alone rather than an oversight: the {@link ContextAccessor}
 * dependency above is an internal package, so keeping this off the deployed request path means a
 * Temporal upgrade cannot break payment initialisation in a real environment. With the property
 * off, both Update-With-Start callers - Secure Fields init and Mobile SDK init - propagate no
 * context, exactly as before.
 */
public class UpdateWithStartTracingInterceptor extends WorkflowClientInterceptorBase {

  private final OpenTracingOptions options;
  private final ContextAccessor contextAccessor;

  public UpdateWithStartTracingInterceptor(OpenTracingOptions options) {
    this.options = options;
    this.contextAccessor = new ContextAccessor(options);
  }

  @Override
  public WorkflowClientCallsInterceptor workflowClientCallsInterceptor(
      WorkflowClientCallsInterceptor next) {
    return new WorkflowClientCallsInterceptorBase(next) {
      @Override
      public <R> WorkflowUpdateWithStartOutput<R> updateWithStart(
          WorkflowUpdateWithStartInput<R> input) {
        Tracer tracer = options.getTracer();
        Span activeSpan = tracer.activeSpan();

        // No caller context to carry: leave the call untouched rather than invent a root span.
        if (activeSpan == null) {
          return super.updateWithStart(input);
        }

        return super.updateWithStart(withTracingHeaders(input, activeSpan, tracer));
      }
    };
  }

  /**
   * Rebuilds the input with tracing headers on both halves of the operation.
   *
   * <p>The start half covers the run that Update-With-Start may create; the update half covers
   * the handler itself, which is where the activities are invoked from and therefore what
   * decides the context of the downstream calls.
   */
  private <R> WorkflowClientCallsInterceptor.WorkflowUpdateWithStartInput<R> withTracingHeaders(
      WorkflowClientCallsInterceptor.WorkflowUpdateWithStartInput<R> input,
      Span activeSpan,
      Tracer tracer) {
    WorkflowClientCallsInterceptor.WorkflowStartInput start = input.getWorkflowStartInput();
    WorkflowClientCallsInterceptor.StartUpdateInput<R> update = input.getStartUpdateInput();

    return new WorkflowClientCallsInterceptor.WorkflowUpdateWithStartInput<>(
        new WorkflowClientCallsInterceptor.WorkflowStartInput(
            start.getWorkflowId(),
            start.getWorkflowType(),
            tracingHeader(start.getHeader(), activeSpan, tracer),
            start.getArguments(),
            start.getOptions()),
        new WorkflowClientCallsInterceptor.StartUpdateInput<>(
            update.getWorkflowExecution(),
            update.getWorkflowType(),
            update.getUpdateName(),
            tracingHeader(update.getHeader(), activeSpan, tracer),
            update.getUpdateId(),
            update.getArguments(),
            update.getResultClass(),
            update.getResultType(),
            update.getFirstExecutionRunId(),
            update.getWaitPolicy()));
  }

  /** Copies the header so the span context is written into a map known to be mutable. */
  private Header tracingHeader(Header source, Span activeSpan, Tracer tracer) {
    Header header = new Header(new HashMap<>(source.getValues()));
    contextAccessor.writeSpanContextToHeader(activeSpan.context(), header, tracer);
    return header;
  }
}
