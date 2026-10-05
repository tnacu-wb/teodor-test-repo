package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.kiosk.ErrorCode;

public class RoomAllocationException extends AbstractInternalException {

  public RoomAllocationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
