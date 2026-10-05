package uk.co.whitbread.dashboard.infrastructure.rest.client.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class ContentClientException extends AbstractInternalException {

  public ContentClientException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}