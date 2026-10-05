package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class EckohException extends AbstractInternalException {

  public EckohException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public EckohException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
