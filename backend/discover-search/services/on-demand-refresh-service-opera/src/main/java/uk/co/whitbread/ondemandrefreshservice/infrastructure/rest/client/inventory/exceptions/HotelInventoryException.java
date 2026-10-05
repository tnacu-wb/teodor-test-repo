package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.inventory.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ondemandrefreshservice.ErrorCode;

public class HotelInventoryException extends AbstractInternalException {

  public HotelInventoryException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}