package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a downstream service returns a non-2xx HTTP response.
 * Maps to 502 Bad Gateway (GATEWAY_ERROR).
 */
public class GatewayException extends RuntimeException {

  public GatewayException(String message) {
    super(message);
  }

  public GatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
