package uk.co.whitbread.reservation.infrastructure.rest.client.changelog.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class ChangeLogException extends AbstractInternalException {

  public ChangeLogException(String message) {
    super(message, message, 0);
  }

  public ChangeLogException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
