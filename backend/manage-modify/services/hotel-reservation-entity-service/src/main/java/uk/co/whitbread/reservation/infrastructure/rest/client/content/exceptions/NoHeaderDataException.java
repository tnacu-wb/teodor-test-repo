package uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class NoHeaderDataException extends AbstractNotFoundException {

  public NoHeaderDataException(String message) {
    super(message, message, 0);
  }

  public NoHeaderDataException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
