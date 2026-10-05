package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.digitalkey.ErrorCode;

public class RoomAllocationException extends AbstractInternalException {

  public RoomAllocationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
