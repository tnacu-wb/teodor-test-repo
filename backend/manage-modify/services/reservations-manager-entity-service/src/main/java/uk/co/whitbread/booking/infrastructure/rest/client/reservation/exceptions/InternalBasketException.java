package uk.co.whitbread.booking.infrastructure.rest.client.reservation.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class InternalBasketException extends AbstractInternalException {

  public InternalBasketException(String globalErrTextTemplate, String debugMessage,
      Throwable cause, int errorCode) {
    super(globalErrTextTemplate, debugMessage, cause, errorCode);
  }
}
