package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions;

public class SnowdropLookupException extends RuntimeException {

  public SnowdropLookupException(String message, Throwable cause) {
    super(message, cause);
  }

  public SnowdropLookupException(String message) {
    super(message);
  }
}
