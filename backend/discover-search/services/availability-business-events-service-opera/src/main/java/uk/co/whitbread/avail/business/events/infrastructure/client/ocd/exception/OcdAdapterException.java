package uk.co.whitbread.avail.business.events.infrastructure.client.ocd.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OcdAdapterException extends AbstractInternalException {

  public OcdAdapterException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
