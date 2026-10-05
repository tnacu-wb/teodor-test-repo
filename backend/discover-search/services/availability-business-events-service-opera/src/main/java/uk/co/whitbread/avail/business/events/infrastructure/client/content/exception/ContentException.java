package uk.co.whitbread.avail.business.events.infrastructure.client.content.exception;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class ContentException extends AbstractInternalException {

  public ContentException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
