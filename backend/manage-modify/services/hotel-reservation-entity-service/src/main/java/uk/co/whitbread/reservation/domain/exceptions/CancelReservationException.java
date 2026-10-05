package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class CancelReservationException extends AbstractInternalException {

  public CancelReservationException(uk.co.whitbread.reservation.ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public CancelReservationException(uk.co.whitbread.reservation.ErrorCode error,
      String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}