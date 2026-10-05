package uk.co.whitbread.ohip.infrastructure.rest.client.checkin.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class CheckInException extends AbstractInternalException {

  public CheckInException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
