package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.kiosk.ErrorCode;

public class ProfileException extends AbstractInternalException {

  public ProfileException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }

}
