package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions;

public class EventMessageListenerException extends RuntimeException {

  public EventMessageListenerException(String message, Throwable cause) {
    super(message, cause);
  }

  public EventMessageListenerException(String message) {
    super(message);
  }
}

