package uk.co.whitbread.ohip.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class ReservationNotFoundException extends AbstractInternalException {

  public ReservationNotFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
