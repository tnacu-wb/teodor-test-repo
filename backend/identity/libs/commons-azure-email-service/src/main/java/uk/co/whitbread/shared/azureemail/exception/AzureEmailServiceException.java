package uk.co.whitbread.shared.azureemail.exception;

public class AzureEmailServiceException extends RuntimeException {

  public AzureEmailServiceException() {
  }

  public AzureEmailServiceException(String message) {
    super(message);
  }

  public AzureEmailServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public AzureEmailServiceException(Throwable cause) {
    super(cause);
  }
}
