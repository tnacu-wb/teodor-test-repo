package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a downstream service (e.g. Datatrans) is unreachable.
 */
public class ServiceUnavailableException extends RuntimeException {

  public ServiceUnavailableException(String message) {
    super(message);
  }

  public ServiceUnavailableException(String message, Throwable cause) {
    super(message, cause);
  }
}
