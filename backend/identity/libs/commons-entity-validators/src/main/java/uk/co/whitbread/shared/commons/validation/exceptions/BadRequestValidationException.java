package uk.co.whitbread.shared.commons.validation.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.shared.commons.validation.enums.ValidationError;

public class BadRequestValidationException extends AbstractBadRequestException {

  public BadRequestValidationException(ValidationError error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
