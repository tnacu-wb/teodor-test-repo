package uk.co.whitbread.reservation.infrastructure.rest.client.reservation.exception;

public class ResponseParsingException extends RuntimeException {

  public ResponseParsingException(String message, Exception e) {
    super(message, e);
  }

}
