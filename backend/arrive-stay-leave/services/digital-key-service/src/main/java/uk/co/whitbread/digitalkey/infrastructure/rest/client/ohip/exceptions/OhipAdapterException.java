package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OhipAdapterException extends AbstractInternalException {

  public OhipAdapterException(String message, String debugMessage, Throwable clause,
                              int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}