package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions;

public class AvailabilityCacheDbException extends RuntimeException {

  public AvailabilityCacheDbException(String message, Throwable cause) {
    super(message, cause);
  }

  public AvailabilityCacheDbException(String message) {
    super(message);
  }
}
