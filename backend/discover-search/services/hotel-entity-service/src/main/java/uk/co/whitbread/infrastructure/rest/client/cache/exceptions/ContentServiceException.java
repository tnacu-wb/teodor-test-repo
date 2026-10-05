package uk.co.whitbread.infrastructure.rest.client.cache.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class ContentServiceException extends AbstractInternalException {

  public ContentServiceException(String message, String debugMessage, Throwable clause,
                                 int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
