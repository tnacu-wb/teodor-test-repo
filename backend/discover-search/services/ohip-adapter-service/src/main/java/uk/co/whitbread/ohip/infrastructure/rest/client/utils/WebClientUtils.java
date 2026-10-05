package uk.co.whitbread.ohip.infrastructure.rest.client.utils;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipBadRequestRetryException;
import uk.co.whitbread.ohip.infrastructure.exceptions.RetriesExhaustedException;

@UtilityClass
public class WebClientUtils {

  private static final String BAD_REQUEST = "Bad Request";
  private static final int MAX_RETRIES = 3;
  private static final int MIN_BACKOFF = 3;

  public static void logErrorResponse(Logger log, ClientResponse response) {
    logErrorHeader(log, response);
    Map<String, String> contextMdc = MDC.getCopyOfContextMap();
    response.bodyToMono(String.class)
        .subscribe(body -> {
          if (contextMdc != null) {
            MDC.setContextMap(contextMdc);
          }
          log.error("Response body: {}", body);
        });
  }

  public static void logErrorResponse(Logger log, Object body, ClientResponse response) {
    logErrorHeader(log, response);
    Map<String, String> contextMdc = MDC.getCopyOfContextMap();
    if (contextMdc != null) {
      MDC.setContextMap(contextMdc);
    }

    log.error("Response body: {}", body);
  }

  public static void logErrorHeader(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
  }

  private static boolean isRetryNeeded(Map<?, ?> body) {
    var errorType = body.get("type");
    return errorType != null && BAD_REQUEST.equalsIgnoreCase(errorType.toString());
  }

  public static Mono<Throwable> getOnStatusException(Logger log,
      ClientResponse response, AbstractInternalException customException, ErrorCode errorCode) {
    return response.bodyToMono(Object.class)
        .flatMap(body -> {
          logErrorResponse(log, body, response);
          if (isRetryNeeded((LinkedHashMap<?, ?>) body)) {
            return Mono.error(
                new OhipBadRequestRetryException(errorCode, customException.getDebugMessage()));
          }

          return Mono.error(customException);
        });
  }

  public static RetryBackoffSpec getRetrySpec(AbstractInternalException initialException) {
    return Retry.backoff(MAX_RETRIES, Duration.ofSeconds(MIN_BACKOFF))
        .filter((throwable ->
            throwable instanceof OhipBadRequestRetryException
                || throwable instanceof PrematureCloseException
                || (throwable instanceof WebClientRequestException
                && throwable.getCause() instanceof PrematureCloseException)))
        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> initialException);
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
            new RetriesExhaustedException(ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION,
                retrySignal.failure()));
  }

}
