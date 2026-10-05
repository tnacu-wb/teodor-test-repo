package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.reservation.ErrorCode;

public class GenericReservationException extends AbstractInternalException {

  public GenericReservationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public GenericReservationException(ErrorCode error, String debugMessage,
      Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }

}
