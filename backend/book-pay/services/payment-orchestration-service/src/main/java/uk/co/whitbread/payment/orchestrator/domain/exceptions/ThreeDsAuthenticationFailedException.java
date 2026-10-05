package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when 3-D Secure authentication fails for a transaction.
 */
public class ThreeDsAuthenticationFailedException extends RuntimeException {

  public ThreeDsAuthenticationFailedException(String message) {
    super(message);
  }

  public ThreeDsAuthenticationFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
