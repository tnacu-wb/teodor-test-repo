package uk.co.whitbread.availabilitycacheservice.infrastructure.client.exception;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class ContentException extends AbstractInternalException {

  public ContentException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
