package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;
import uk.co.whitbread.ohip.ErrorCode;

public class HotelReservationNotFound extends AbstractNotFoundException {

  public HotelReservationNotFound(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
