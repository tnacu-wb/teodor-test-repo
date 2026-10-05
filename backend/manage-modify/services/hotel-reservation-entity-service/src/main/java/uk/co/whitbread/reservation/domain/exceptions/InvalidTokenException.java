package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.reservation.ErrorCode;

public class InvalidTokenException extends AbstractBadRequestException {

  public InvalidTokenException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
