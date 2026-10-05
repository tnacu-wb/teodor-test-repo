package uk.co.whitbread.booking.infrastructure.rest.client.booking.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class InternalBookingException extends AbstractInternalException {

  public InternalBookingException(String globalErrTextTemplate, String debugMessage,
      int errorCode) {
    super(globalErrTextTemplate, debugMessage, errorCode);
  }
}
