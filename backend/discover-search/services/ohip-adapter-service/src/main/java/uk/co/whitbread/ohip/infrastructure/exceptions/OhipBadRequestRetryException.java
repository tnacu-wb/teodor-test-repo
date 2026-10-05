package uk.co.whitbread.ohip.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.ohip.ErrorCode;

public class OhipBadRequestRetryException extends AbstractBadRequestException {
  public OhipBadRequestRetryException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public OhipBadRequestRetryException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
