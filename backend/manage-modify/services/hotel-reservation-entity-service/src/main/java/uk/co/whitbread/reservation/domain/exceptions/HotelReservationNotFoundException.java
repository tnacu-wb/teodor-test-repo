package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class HotelReservationNotFoundException extends AbstractNotFoundException {

  public HotelReservationNotFoundException(String message) {
    super(message, message, 0);
  }

  public HotelReservationNotFoundException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
