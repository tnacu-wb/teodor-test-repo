package uk.co.whitbread.content.infrastructure.config.component;

import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.springframework.boot.data.metrics.AutoTimer;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

@Slf4j
public class MetricsFilterFunction implements ExchangeFilterFunction {

  private static final String METRICS_WEBCLIENT_START_TIME = MetricsFilterFunction.class.getName()
      + ".START_TIME";

  private final AutoTimer autoTimer;

  /**
   * Create a new {@code MetricsWebClientFilterFunction}.
   *
   * @param autoTimer the auto-timer configuration or {@code null} to disable
   * @since 2.2.0
   */
  public MetricsFilterFunction(AutoTimer autoTimer) {
    this.autoTimer = (autoTimer != null) ? autoTimer : AutoTimer.DISABLED;
  }

  @Override
  public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
    if (!this.autoTimer.isEnabled()) {
      return next.exchange(request);
    }
    return next.exchange(request).as(responseMono -> instrumentResponse(request, responseMono))
        .contextWrite(this::putStartTime);
  }

  private Mono<ClientResponse> instrumentResponse(ClientRequest request, Mono<ClientResponse> responseMono) {
    final AtomicBoolean responseReceived = new AtomicBoolean();
    return Mono.deferContextual(ctx -> responseMono.doOnEach(signal -> {
      if (signal.isOnNext() || signal.isOnError()) {
        responseReceived.set(true);
        var duration = System.currentTimeMillis() - getStartTime(ctx);
        log.info(new ObjectAppendingMarker("webclient_request", createIndexableObject(request, duration)),
            "Request for {} ended in {} ms", request.url(), duration, signal.getThrowable());
      }
    }).doFinally(signalType -> {
      if (!responseReceived.get() && SignalType.CANCEL.equals(signalType)) {
        var duration = System.currentTimeMillis() - getStartTime(ctx);
        log.warn(new ObjectAppendingMarker("webclient_request", createIndexableObject(request, duration)),
            "Request for {} ended abruptly in {} ms", request.url(), duration);
      }
    }));
  }

  private Long getStartTime(ContextView context) {
    return context.get(METRICS_WEBCLIENT_START_TIME);
  }

  private Context putStartTime(Context context) {
    return context.put(METRICS_WEBCLIENT_START_TIME, System.currentTimeMillis());
  }

  private LoggingIndex createIndexableObject(ClientRequest request, long duration) {
    return LoggingIndex.builder()
        .remoteService("AEM")
        .httpMethod(request.method().name())
        .url(request.url().toString())
        .path(request.url().getPath())
        .duration(duration)
        .build();
  }

}
