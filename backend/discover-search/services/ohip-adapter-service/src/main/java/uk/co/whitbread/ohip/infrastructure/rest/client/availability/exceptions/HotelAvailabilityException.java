package uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class HotelAvailabilityException extends AbstractInternalException {

  public HotelAvailabilityException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public HotelAvailabilityException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}