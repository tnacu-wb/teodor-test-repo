package uk.co.whitbread.ohip.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.ohip.ErrorCode;

public class ParameterMismatchException extends AbstractBadRequestException {

  public ParameterMismatchException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}
