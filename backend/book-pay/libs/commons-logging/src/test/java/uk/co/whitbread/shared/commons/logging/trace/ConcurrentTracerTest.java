package uk.co.whitbread.shared.commons.logging.trace;

import brave.Tracing;
import brave.baggage.BaggageField;
import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationConfig;
import brave.propagation.StrictCurrentTraceContext;
import io.micrometer.tracing.BaggageInScope;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BravePropagator;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import io.micrometer.tracing.brave.bridge.W3CPropagation;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

class ConcurrentTracerTest {

  StrictCurrentTraceContext currentTraceContext = new StrictCurrentTraceContext();
  BraveBaggageManager braveBaggageManager = new BraveBaggageManager();

  Tracing tracing = Tracing.newBuilder()
      .propagationFactory(BaggagePropagation.newFactoryBuilder(new W3CPropagation())
          .add(BaggagePropagationConfig.SingleBaggageField.remote(BaggageField.create("x-amzn-trace-id")))
          .build())
      .currentTraceContext(currentTraceContext)
      .build();

  Tracer tracer = new BraveTracer(tracing.tracer(), new BraveCurrentTraceContext(tracing.currentTraceContext()),
      braveBaggageManager);

  BravePropagator bravePropagator = new BravePropagator(tracing);

  ConcurrentTracer concurrentTracer = new ConcurrentTracer(tracer);

  @AfterEach
  void cleanup() {
    tracing.close();
    currentTraceContext.close();
  }

  @Test
  void should_propagate_context_with_trace_and_baggage() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (BaggageInScope baggage = Objects.requireNonNull(tracer.getBaggage(span.context(), "x-amzn-trace-id"))
        .makeCurrent()) {
      BDDAssertions.then(baggage.get(span.context()))
          .isEqualTo("651eb80425bd97ba27be0b57af8f3027");
      BDDAssertions.then(span.context().traceId()).isEqualTo("3e425f2373d89640bde06e8285e7bf88");
    }
  }

  @Test
  void should_propagate_context_with_trace_in_java_streams() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      IntStream.range(1, 10)
          .parallel()
          .forEach(concurrentTracer.wrap(
              (IntConsumer) value -> BDDAssertions.then(
                      Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                  .isEqualTo("3e425f2373d89640bde06e8285e7bf88")));
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_java_streams() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      IntStream.range(1, 10)
          .parallel()
          .forEach(concurrentTracer.wrap(
              (IntConsumer) value -> BDDAssertions.then(tracer.currentSpan()).isNull()));
    }
  }

  @Test
  void should_propagate_context_with_trace_in_completable_future_with_wrapped_runnable() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
            BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                .isEqualTo("3e425f2373d89640bde06e8285e7bf88");
          }))
          .whenComplete(concurrentTracer.wrap(
              (t, u) -> BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                  .isEqualTo("3e425f2373d89640bde06e8285e7bf88")))
          .get();
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_completable_future_with_wrapped_runnable() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      CompletableFuture.runAsync(
              concurrentTracer.wrap(() -> BDDAssertions.then(tracer.currentSpan()).isNull()))
          .whenComplete(concurrentTracer.wrap(
              (t, u) -> BDDAssertions.then(tracer.currentSpan()).isNull()))
          .get();
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_propagate_context_with_trace_in_completable_future_and_wrapped_executor() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      CompletableFuture.runAsync(
              () -> BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                  .isEqualTo("3e425f2373d89640bde06e8285e7bf88"),
              concurrentTracer.wrap(ForkJoinPool.commonPool()))
          .whenComplete(
              (t, u) -> BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                  .isEqualTo("3e425f2373d89640bde06e8285e7bf88"))
          .get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_completable_future_with_wrapped_executor() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      CompletableFuture.runAsync(() -> BDDAssertions.then(tracer.currentSpan())
                  .isNull(),
              concurrentTracer.wrap(ForkJoinPool.commonPool()))
          .whenComplete(
              (t, u) -> BDDAssertions.then(tracer.currentSpan()).isNull())
          .get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_propagate_context_with_trace_in_wrapped_consumer() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      Stream.of("a", "b")
          .parallel()
          .forEach(concurrentTracer.wrap((Consumer<String>) value -> BDDAssertions.then(
                  Objects.requireNonNull(tracer.currentSpan()).context().traceId())
              .isEqualTo("3e425f2373d89640bde06e8285e7bf88")));
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_wrapped_consumer() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      Stream.of("a", "b")
          .parallel()
          .forEach(
              concurrentTracer.wrap((Consumer<String>) value -> BDDAssertions.then(tracer.currentSpan())
                  .isNull()));
    }
  }

  @Test
  void should_propagate_context_with_trace_in_wrapped_function() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      var list = Stream.of("a", "b")
          .parallel()
          .map(concurrentTracer.wrap(value -> {
            BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                .isEqualTo("3e425f2373d89640bde06e8285e7bf88");
            return value;
          }))
          .collect(Collectors.toList());
      BDDAssertions.then(list).isNotNull();
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_wrapped_function() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      var list = Stream.of("a", "b")
          .parallel()
          .map(concurrentTracer.wrap(value -> {
            BDDAssertions.then(tracer.currentSpan())
                .isNull();
            return value;
          }))
          .collect(Collectors.toList());
      BDDAssertions.then(list).isNotNull();
    }
  }

  @Test
  void should_propagate_context_with_trace_in_wrapped_supplier() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      var list = Stream.of("a", "b")
          .parallel()
          .collect(concurrentTracer.wrap((Supplier<StringBuilder>) () -> {
            BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                .isEqualTo("3e425f2373d89640bde06e8285e7bf88");
            return new StringBuilder();
          }), StringBuilder::append, StringBuilder::append);
      BDDAssertions.then(list).isNotNull();
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_wrapped_supplier() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      var list = Stream.of("a", "b")
          .parallel()
          .collect(concurrentTracer.wrap((Supplier<StringBuilder>) () -> {
            BDDAssertions.then(tracer.currentSpan())
                .isNull();
            return new StringBuilder();
          }), StringBuilder::append, StringBuilder::append);
      BDDAssertions.then(list).isNotNull();
    }
  }

  @Test
  void should_propagate_context_with_trace_in_wrapped_callable() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(concurrentTracer.wrap((Callable<String>) () -> {
            BDDAssertions.then(Objects.requireNonNull(tracer.currentSpan()).context().traceId())
                .isEqualTo("3e425f2373d89640bde06e8285e7bf88");
            return null;
          }))
          .get();
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_not_propagate_context_with_trace_in_wrapped_callable() {
    try (Tracer.SpanInScope ignored = tracer.withSpan(null)) {
      ExecutorService executorService = Executors.newSingleThreadExecutor();
      executorService.submit(concurrentTracer.wrap((Callable<String>) () -> {
            BDDAssertions.then(tracer.currentSpan())
                .isNull();
            return null;
          }))
          .get();
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_cary_valid_tracer() {
    Map<String, String> carrier = new HashMap<>();
    carrier.put("traceparent", "00-3e425f2373d89640bde06e8285e7bf88-9a5fdefae3abb440-00");
    carrier.put("x-amzn-trace-id", "651eb80425bd97ba27be0b57af8f3027");
    Span.Builder extract = bravePropagator.extract(carrier, Map::get);
    Span span = extract.start();
    try (Tracer.SpanInScope ignored = concurrentTracer.getTracer().withSpan(span)) {
      BDDAssertions.then(Objects.requireNonNull(concurrentTracer.getTracer().currentSpan()).context().traceId())
          .isEqualTo("3e425f2373d89640bde06e8285e7bf88");
    }
  }
}