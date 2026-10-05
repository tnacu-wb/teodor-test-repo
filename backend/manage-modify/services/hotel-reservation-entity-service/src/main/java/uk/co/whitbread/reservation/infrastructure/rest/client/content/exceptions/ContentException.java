package uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class ContentException extends AbstractInternalException {

  public ContentException(String message) {
    super(message, message, 0);
  }

  public ContentException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
