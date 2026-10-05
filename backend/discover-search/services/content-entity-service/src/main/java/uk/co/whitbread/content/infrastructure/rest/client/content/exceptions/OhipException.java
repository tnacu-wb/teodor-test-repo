package uk.co.whitbread.content.infrastructure.rest.client.content.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OhipException extends AbstractInternalException {

  public OhipException(String message, String debugMessage, Throwable clause, int errorCode) {
    super(message, debugMessage, clause, errorCode);
  }

}
