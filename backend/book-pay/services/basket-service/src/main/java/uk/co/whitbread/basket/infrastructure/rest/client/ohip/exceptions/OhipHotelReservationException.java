package uk.co.whitbread.basket.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OhipHotelReservationException extends AbstractInternalException {

  public OhipHotelReservationException() {
    super("", "", 0);
  }

  public OhipHotelReservationException(String message) {
    super(message, message, 0);
  }

  public OhipHotelReservationException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}