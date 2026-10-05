package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a transaction cannot be found or has expired.
 */
public class TransactionNotFoundException extends RuntimeException {

  public TransactionNotFoundException(String message) {
    super(message);
  }

  public TransactionNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
