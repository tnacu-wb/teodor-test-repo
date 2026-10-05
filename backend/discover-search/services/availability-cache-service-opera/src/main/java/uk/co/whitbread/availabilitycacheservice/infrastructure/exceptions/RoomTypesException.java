package uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions;

import org.springframework.http.HttpStatus;

public class RoomTypesException extends RuntimeException {

  private final HttpStatus status;

  public RoomTypesException(final String message, final HttpStatus status) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
