package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class GenericBadRequestException extends AbstractBadRequestException {

  public GenericBadRequestException(uk.co.whitbread.reservation.ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
