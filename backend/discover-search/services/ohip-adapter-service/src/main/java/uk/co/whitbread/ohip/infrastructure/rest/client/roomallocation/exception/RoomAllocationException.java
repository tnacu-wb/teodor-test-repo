package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class RoomAllocationException extends AbstractInternalException {

  public RoomAllocationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
