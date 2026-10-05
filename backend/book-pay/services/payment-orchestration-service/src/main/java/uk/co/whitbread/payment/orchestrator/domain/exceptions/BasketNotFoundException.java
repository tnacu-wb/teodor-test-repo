package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when a basket cannot be found for the given identifier.
 */
public class BasketNotFoundException extends RuntimeException {

  public BasketNotFoundException(String message) {
    super(message);
  }

  public BasketNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
