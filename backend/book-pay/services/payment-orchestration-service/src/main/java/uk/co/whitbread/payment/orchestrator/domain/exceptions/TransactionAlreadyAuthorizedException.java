package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when an authorization is attempted on a transaction that has already been authorized.
 */
public class TransactionAlreadyAuthorizedException extends RuntimeException {

  public TransactionAlreadyAuthorizedException(String message) {
    super(message);
  }

  public TransactionAlreadyAuthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
}
