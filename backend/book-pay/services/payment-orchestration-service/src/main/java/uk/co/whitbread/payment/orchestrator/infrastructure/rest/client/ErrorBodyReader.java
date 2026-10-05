package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * Reads downstream error response bodies for diagnostic logging.
 *
 * <p>{@code RestClient} status handlers receive the raw {@link ClientHttpResponse},
 * whose body may be absent, empty, or already consumed. Logging must never be the
 * reason a request fails, so every failure mode collapses to a placeholder string.
 */
public final class ErrorBodyReader {

  private static final String NO_BODY = "(no body)";

  private ErrorBodyReader() {
  }

  /**
   * Reads the response body as UTF-8, returning {@code "(no body)"} when it is
   * empty or cannot be read.
   *
   * @param response the error response to read
   * @return the body text, or {@code "(no body)"}
   */
  public static String read(ClientHttpResponse response) {
    try {
      String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
      return body.isBlank() ? NO_BODY : body;
    } catch (IOException e) {
      return NO_BODY;
    }
  }
}
