package uk.co.whitbread.infrastructure.rest.client.distance.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class SnowDropException extends AbstractInternalException {

  public SnowDropException(String message, String debugMessage, Throwable clause,
                           int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public SnowDropException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
