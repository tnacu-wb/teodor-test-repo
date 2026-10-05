package uk.co.whitbread.booking.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelReservationOhipException extends AbstractInternalException {

  public HotelReservationOhipException(String globalErrTextTemplate, String debugMessage,
      Throwable cause, int errorCode) {
    super(globalErrTextTemplate, debugMessage, cause, errorCode);
  }
}
