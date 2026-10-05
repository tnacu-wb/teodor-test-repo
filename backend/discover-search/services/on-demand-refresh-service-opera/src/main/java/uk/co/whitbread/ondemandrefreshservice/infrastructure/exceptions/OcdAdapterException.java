package uk.co.whitbread.ondemandrefreshservice.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OcdAdapterException extends AbstractInternalException {

  public OcdAdapterException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
