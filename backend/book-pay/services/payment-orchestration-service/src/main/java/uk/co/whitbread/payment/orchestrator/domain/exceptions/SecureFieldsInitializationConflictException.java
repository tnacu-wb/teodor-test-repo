package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a Secure Fields initialization conflicts with workflow state.
 */
public class SecureFieldsInitializationConflictException extends RuntimeException {

  public SecureFieldsInitializationConflictException(String message) {
    super(message);
  }

  public SecureFieldsInitializationConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}
