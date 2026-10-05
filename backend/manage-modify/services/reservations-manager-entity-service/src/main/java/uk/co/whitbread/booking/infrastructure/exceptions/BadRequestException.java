package uk.co.whitbread.booking.infrastructure.exceptions;

import uk.co.whitbread.booking.domain.model.exceptions.ErrorCode;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class BadRequestException extends AbstractBadRequestException {

  public BadRequestException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage,  error.getCode());
  }
}
