package uk.co.whitbread.availabilitycacheservice.infrastructure.client.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class NoHeaderDataException extends AbstractNotFoundException {

  public NoHeaderDataException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
