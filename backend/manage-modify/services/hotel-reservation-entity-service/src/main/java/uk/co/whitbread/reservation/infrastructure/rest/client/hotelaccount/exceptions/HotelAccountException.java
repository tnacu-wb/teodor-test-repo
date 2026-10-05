package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelAccountException extends AbstractInternalException {

  public HotelAccountException(String message) {
    super(message, message, 0);
  }

  public HotelAccountException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}