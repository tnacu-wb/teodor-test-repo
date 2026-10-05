package uk.co.whitbread.ohip.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class PolicyCodeMismatchException extends AbstractInternalException {

  public PolicyCodeMismatchException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}
