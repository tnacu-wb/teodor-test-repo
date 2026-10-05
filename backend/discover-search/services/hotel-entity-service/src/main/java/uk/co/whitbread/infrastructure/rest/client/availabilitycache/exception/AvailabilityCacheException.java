package uk.co.whitbread.infrastructure.rest.client.availabilitycache.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class AvailabilityCacheException extends AbstractInternalException {
  public AvailabilityCacheException(String message, String debugMessage, Throwable clause,
                                    int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public AvailabilityCacheException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
