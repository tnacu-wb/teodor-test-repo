package uk.co.whitbread.payment.orchestrator.domain.exceptions;

/**
 * Thrown when the Datatrans API returns a non-2xx response.
 */
public class DatatransGatewayException extends RuntimeException {

  public DatatransGatewayException(String message) {
    super(message);
  }

  public DatatransGatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
