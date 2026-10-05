package uk.co.whitbread.shared.commons.logging.trace;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class ConcurrentTracer {

  private final Tracer tracer;

  public Runnable wrap(Runnable runnable) {
    var currentTraceContext = this.tracer.currentTraceContext();
    if (Objects.nonNull(currentTraceContext)) {
      return currentTraceContext.wrap(runnable);
    } else {
      return runnable;
    }
  }

  public <R> Callable<R> wrap(Callable<R> callable) {
    var currentTraceContext = this.tracer.currentTraceContext();
    if (Objects.nonNull(currentTraceContext)) {
      return currentTraceContext.wrap(callable);
    } else {
      return callable;
    }
  }

  public ExecutorService wrap(ExecutorService executorService) {
    var currentTraceContext = this.tracer.currentTraceContext();
    if (Objects.nonNull(currentTraceContext)) {
      return currentTraceContext.wrap(executorService);
    } else {
      return executorService;
    }
  }

  public <T, U> BiConsumer<T, U> wrap(BiConsumer<T, U> untracedBiConsumer) {
    return new BiConsumer<>() {
      private final Span invocationSpan = ConcurrentTracer.this.tracer.currentSpan();

      @Override
      public void accept(T t, U u) {
        try (Tracer.SpanInScope ignored = ConcurrentTracer.this.tracer.withSpan(invocationSpan)) {
          untracedBiConsumer.accept(t, u);
        }
      }
    };
  }

  public IntConsumer wrap(IntConsumer untracedIntConsumer) {
    return new IntConsumer() {
      private final Span invocationSpan = ConcurrentTracer.this.tracer.currentSpan();

      @Override
      public void accept(int value) {
        try (Tracer.SpanInScope ignored = ConcurrentTracer.this.tracer.withSpan(invocationSpan)) {
          untracedIntConsumer.accept(value);
        }
      }
    };
  }

  public <T> Supplier<T> wrap(Supplier<T> untracedSupplier) {
    return new Supplier<>() {
      private final Span invocationSpan = ConcurrentTracer.this.tracer.currentSpan();

      @Override
      public T get() {
        try (Tracer.SpanInScope ignored = ConcurrentTracer.this.tracer.withSpan(invocationSpan)) {
          return untracedSupplier.get();
        }
      }
    };
  }

  public <T> Consumer<T> wrap(Consumer<T> untracedConsumer) {
    return new Consumer<>() {
      private final Span invocationSpan = ConcurrentTracer.this.tracer.currentSpan();

      @Override
      public void accept(T t) {
        try (Tracer.SpanInScope ignored = ConcurrentTracer.this.tracer.withSpan(invocationSpan)) {
          untracedConsumer.accept(t);
        }
      }
    };
  }

  public <T, R> Function<T, R> wrap(Function<T, R> untracedFunction) {
    return new Function<>() {
      private final Span invocationSpan = ConcurrentTracer.this.tracer.currentSpan();

      @Override
      public R apply(T t) {
        try (Tracer.SpanInScope ignored = ConcurrentTracer.this.tracer.withSpan(invocationSpan)) {
          return untracedFunction.apply(t);
        }
      }
    };
  }
}
