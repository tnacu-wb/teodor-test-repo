package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelReservationOhipException extends AbstractInternalException {

  public HotelReservationOhipException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}