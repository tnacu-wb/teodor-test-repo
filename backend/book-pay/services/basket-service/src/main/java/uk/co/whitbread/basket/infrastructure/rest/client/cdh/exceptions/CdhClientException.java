package uk.co.whitbread.basket.infrastructure.rest.client.cdh.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class CdhClientException extends AbstractInternalException {

  public CdhClientException(String message, String debugMessage, Throwable clause,
                            int errorCode) {
    super(message, debugMessage, clause, errorCode);
  }
}
