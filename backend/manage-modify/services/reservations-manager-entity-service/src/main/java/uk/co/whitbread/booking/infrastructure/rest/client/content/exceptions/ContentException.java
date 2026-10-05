package uk.co.whitbread.booking.infrastructure.rest.client.content.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class ContentException extends AbstractInternalException {

  public ContentException(String globalErrTextTemplate, String debugMessage, Throwable cause,
      int errorCode) {
    super(globalErrTextTemplate, debugMessage, cause, errorCode);
  }
}
