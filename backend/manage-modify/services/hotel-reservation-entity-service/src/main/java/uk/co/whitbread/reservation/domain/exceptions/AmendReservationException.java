package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class AmendReservationException extends AbstractInternalException {

  public AmendReservationException(uk.co.whitbread.reservation.ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public AmendReservationException(uk.co.whitbread.reservation.ErrorCode error,
      String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
