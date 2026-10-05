package uk.co.whitbread.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OhipClientException extends AbstractInternalException {
  public OhipClientException(String message, String debugMessage, Throwable clause,
                             int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
