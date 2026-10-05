package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class UpdateBusinessItemsException extends AbstractInternalException {

  public UpdateBusinessItemsException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
