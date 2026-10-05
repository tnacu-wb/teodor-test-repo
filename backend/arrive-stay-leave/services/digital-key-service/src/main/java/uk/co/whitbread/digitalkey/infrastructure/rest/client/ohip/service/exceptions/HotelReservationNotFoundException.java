package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class HotelReservationNotFoundException extends AbstractNotFoundException {


  public HotelReservationNotFoundException(String message, String debugMessage, Throwable clause,
                                           int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
