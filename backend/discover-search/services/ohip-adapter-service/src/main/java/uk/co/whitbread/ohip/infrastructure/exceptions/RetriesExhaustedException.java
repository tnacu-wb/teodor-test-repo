package uk.co.whitbread.ohip.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class RetriesExhaustedException extends AbstractInternalException {

  public RetriesExhaustedException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public RetriesExhaustedException(ErrorCode error, Throwable cause) {
    super(error.getMessage(), cause.getMessage(), cause, error.getCode());
  }
}