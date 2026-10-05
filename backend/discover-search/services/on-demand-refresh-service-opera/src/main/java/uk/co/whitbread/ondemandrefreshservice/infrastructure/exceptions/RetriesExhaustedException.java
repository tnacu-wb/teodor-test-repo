package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

public class RetriesExhaustedException extends RuntimeException {
  public RetriesExhaustedException(String message, Throwable cause) {
    super(message, cause);
  }

  public RetriesExhaustedException(String message) {
    super(message);
  }
}