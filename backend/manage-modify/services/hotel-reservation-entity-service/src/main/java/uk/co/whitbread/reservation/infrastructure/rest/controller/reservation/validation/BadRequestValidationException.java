package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.validation;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.reservation.ErrorCode;

public class BadRequestValidationException extends AbstractBadRequestException {

  public BadRequestValidationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
