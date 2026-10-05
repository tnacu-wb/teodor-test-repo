package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;
import uk.co.whitbread.reservation.ErrorCode;

public class ReservationNotFoundException extends AbstractNotFoundException {
  public ReservationNotFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
