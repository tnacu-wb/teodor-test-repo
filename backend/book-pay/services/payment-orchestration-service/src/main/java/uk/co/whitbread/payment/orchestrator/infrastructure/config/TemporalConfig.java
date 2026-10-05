package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.opentracingshim.OpenTracingShim;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.common.interceptors.WorkflowClientInterceptor;
import io.temporal.opentracing.OpenTracingClientInterceptor;
import io.temporal.opentracing.OpenTracingOptions;
import io.temporal.opentracing.OpenTracingWorkerInterceptor;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.co.whitbread.payment.orchestrator.domain.workflow.PaymentWorkflowImpl;
import uk.co.whitbread.payment.orchestrator.infrastructure.temporal.PaymentActivitiesImpl;
import uk.co.whitbread.payment.orchestrator.infrastructure.temporal.UpdateWithStartTracingInterceptor;

/**
 * Temporal configuration for workflow client and worker registration.
 *
 * <p>Connects to the Temporal server and creates a single worker subscribed to the
 * {@code payment-workflows} task queue, registering the unified {@link PaymentWorkflowImpl}.
 *
 * <p>Both the client and the worker register Temporal's OpenTracing interceptors, bridged to
 * OpenTelemetry. Without them a workflow or activity begins with an empty OpenTelemetry
 * context: the OpenTelemetry Java agent does not instrument the Temporal SDK, and an activity
 * task reaches the worker as the response to a poll it had already issued rather than as a
 * continuation of the inbound HTTP request. The interceptors carry the context through
 * Temporal's own headers instead, which restores both the W3C trace and any baggage on
 * downstream calls made from activities.
 *
 * <p>Excluded from the {@code integration} profile (used for OpenAPI spec generation)
 * where no Temporal server is available.
 */
@Configuration
@Profile("!integration")
public class TemporalConfig {

  @Value("${temporal.server.host:localhost}")
  private String temporalHost;

  @Value("${temporal.server.port:7233}")
  private int temporalPort;

  @Value("${temporal.task-queue:payment-workflows}")
  private String taskQueue;

  @Value("${temporal.namespace:default}")
  private String namespace;

  /**
   * Bridges the agent-provided OpenTelemetry instance into the OpenTracing API that Temporal's
   * tracing module expects.
   *
   * <p>Inject and extract go through the OpenTelemetry propagators, so the trace context and
   * baggage both cross the Temporal boundary. Temporal publishes no native OpenTelemetry module
   * for Java, so the shim is the supported route.
   */
  @Bean
  public OpenTracingOptions temporalTracingOptions() {
    return OpenTracingOptions.newBuilder()
        .setTracer(OpenTracingShim.createTracerShim(GlobalOpenTelemetry.get()))
        .build();
  }

  @Bean
  public WorkflowServiceStubs workflowServiceStubs() {
    return WorkflowServiceStubs.newServiceStubs(
        WorkflowServiceStubsOptions.newBuilder()
            .setTarget(temporalHost + ":" + temporalPort)
            .build());
  }

  /**
   * Closes the Update-With-Start gap in {@code temporal-opentracing}, which propagates no
   * context on that call. Registered only when
   * {@code temporal.tracing.update-with-start.enabled} is true, which is deliberately the
   * integration stack alone: this interceptor reaches into an internal
   * {@code temporal-opentracing} package, so keeping it off the deployed request path means a
   * Temporal upgrade cannot break payment initialisation in a real environment.
   *
   * <p>With the property off, both Update-With-Start callers - the Secure Fields init path and
   * the Mobile SDK init path - keep their existing behavior of propagating no context, so a
   * payment workflow's spans are not joined to the inbound request there. That is the
   * pre-existing state, not something this bean changes.
   */
  @Bean
  @ConditionalOnProperty(
      name = "temporal.tracing.update-with-start.enabled",
      havingValue = "true")
  public UpdateWithStartTracingInterceptor updateWithStartTracingInterceptor(
      OpenTracingOptions temporalTracingOptions) {
    return new UpdateWithStartTracingInterceptor(temporalTracingOptions);
  }

  /**
   * Creates the workflow client, whose interceptor injects the caller's context into the
   * Temporal headers on start, signal, and query calls.
   *
   * <p>The namespace comes from {@code temporal.namespace} rather than the SDK's implicit
   * {@code default}, so an environment that isolates payment workflows in their own namespace
   * configures it rather than patching this class.
   *
   * <p>Temporal wraps these in list order, so the last element ends up outermost: the
   * Update-With-Start interceptor is the outer one and OpenTracing the inner. That is safe
   * only because this interceptor overrides {@code updateWithStart} alone, a call OpenTracing
   * does not touch. Anything added here that overrides {@code start}, {@code signal}, or
   * {@code query} would run before OpenTracing creates and activates its span and would
   * reparent spans to the caller's span, so order it before the OpenTracing interceptor.
   */
  @Bean
  public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs,
      OpenTracingOptions temporalTracingOptions,
      ObjectProvider<UpdateWithStartTracingInterceptor> updateWithStartTracingInterceptor) {
    List<WorkflowClientInterceptor> interceptors = new ArrayList<>();
    interceptors.add(new OpenTracingClientInterceptor(temporalTracingOptions));
    updateWithStartTracingInterceptor.ifAvailable(interceptors::add);

    return WorkflowClient.newInstance(
        workflowServiceStubs,
        WorkflowClientOptions.newBuilder()
            .setNamespace(namespace)
            .setInterceptors(interceptors.toArray(new WorkflowClientInterceptor[0]))
            .build());
  }

  /**
   * Creates the worker, whose interceptor extracts the context injected by the client so
   * workflow and activity code runs under the caller's trace and baggage.
   *
   * <p>Registration only — the factory is deliberately left stopped here. Polling begins in
   * {@link #workerFactoryLifecycle(WorkerFactory)} once the context has finished refreshing.
   */
  @Bean
  public WorkerFactory workerFactory(WorkflowClient workflowClient,
      PaymentActivitiesImpl paymentActivitiesImpl,
      OpenTracingOptions temporalTracingOptions) {
    WorkerFactory factory = WorkerFactory.newInstance(
        workflowClient,
        WorkerFactoryOptions.newBuilder()
            .setWorkerInterceptors(new OpenTracingWorkerInterceptor(temporalTracingOptions))
            .build());
    Worker worker = factory.newWorker(taskQueue);
    worker.registerWorkflowImplementationTypes(
        PaymentWorkflowImpl.class
    );
    worker.registerActivitiesImplementations(paymentActivitiesImpl);
    return factory;
  }

  /**
   * Starts and stops the worker's task-queue polling on the context lifecycle.
   *
   * <p>Starting inside the {@code workerFactory} bean method meant the worker began accepting
   * activity tasks while the context was still being built: an activity could run against
   * half-initialised singletons, and a bean that failed later left a polling worker behind a
   * context that never came up. Running last on refresh — and stopping first on shutdown —
   * means the worker only polls while the whole application is actually able to serve.
   *
   * <p>{@code shutdown()} matches what the SDK did before via its own JVM shutdown hook; it
   * asks the workers to stop polling and lets in-flight tasks finish rather than killing them.
   */
  @Bean
  public SmartLifecycle workerFactoryLifecycle(WorkerFactory workerFactory) {
    return new SmartLifecycle() {

      private volatile boolean running;

      @Override
      public void start() {
        workerFactory.start();
        running = true;
      }

      @Override
      public void stop() {
        workerFactory.shutdown();
        running = false;
      }

      @Override
      public boolean isRunning() {
        return running;
      }

      @Override
      public int getPhase() {
        return Integer.MAX_VALUE;
      }
    };
  }
}
