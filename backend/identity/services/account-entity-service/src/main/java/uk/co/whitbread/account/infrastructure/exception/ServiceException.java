package uk.co.whitbread.account.infrastructure.exception;

import java.util.List;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class ServiceException extends AbstractInternalException {
  public ServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
