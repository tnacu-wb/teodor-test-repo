package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.util;

import java.time.Duration;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions.RetriesExhaustedException;

@UtilityClass
public class WebClientUtils {

  private static final int MAX_RETRIES = 3;
  private static final int MIN_BACKOFF = 1;

  public static void logErrorResponse(Logger log, ClientResponse response) {
    logErrorHeader(log, response);
    Map<String, String> contextMdc = MDC.getCopyOfContextMap();
    response.bodyToMono(Object.class)
        .subscribe(body -> {
          if (contextMdc != null) {
            MDC.setContextMap(contextMdc);
          }
          log.error("Response body: {}", body);
        });
  }

  public static void logErrorHeader(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
  }

  public static Retry getPrematureCloseExceptionRetrySpec() {
    return Retry.backoff(MAX_RETRIES, Duration.ofSeconds(MIN_BACKOFF))
        .filter(throwable ->
            throwable instanceof PrematureCloseException
                || (
                throwable instanceof WebClientRequestException
                    &&
                    throwable.getCause() instanceof PrematureCloseException))
        .onRetryExhaustedThrow((retrySpec, retrySignal) ->
            new RetriesExhaustedException("Error while trying to complete the operation",
                retrySignal.failure()));
  }

}