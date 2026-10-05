package uk.co.whitbread.shared.auth.webclient;

import java.time.Duration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.PrematureCloseException;
import reactor.util.retry.Retry;
import uk.co.whitbread.shared.auth.config.exception.ErrorCode;
import uk.co.whitbread.shared.auth.exception.RetriesExhaustedException;

@Component
public class WebClientRetryHandler {

  private static final int MAX_RETRIES = 3;
  private static final int MIN_BACKOFF_MS = 1000;

  public Retry getPrematureCloseExceptionRetrySpec() {
    return Retry.backoff(MAX_RETRIES, Duration.ofMillis(MIN_BACKOFF_MS))
        .filter(throwable ->
            throwable instanceof PrematureCloseException
                || (throwable instanceof WebClientRequestException
                    && throwable.getCause() instanceof PrematureCloseException))
        .onRetryExhaustedThrow((retrySpec, retrySignal) ->
            new RetriesExhaustedException(ErrorCode.AUTH0_RETRIES_EXHAUSTED_EXCEPTION,
                retrySignal.failure()));
  }

  public Retry getTransientAndInternalServerErrorRetrySpec() {
    return Retry.backoff(MAX_RETRIES, Duration.ofMillis(MIN_BACKOFF_MS))
        .filter(throwable ->
            throwable instanceof WebClientRequestException
                || throwable instanceof PrematureCloseException
                || (throwable instanceof WebClientResponseException responseException
                    && responseException.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR))
        .onRetryExhaustedThrow((retrySpec, retrySignal) ->
            new RetriesExhaustedException(ErrorCode.AUTH0_RETRIES_EXHAUSTED_EXCEPTION,
                retrySignal.failure()));
  }

}
