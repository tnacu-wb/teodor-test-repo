package uk.co.whitbread.infrastructure.rest.client.availability.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class HotelAvailabilityException extends AbstractInternalException {

  public HotelAvailabilityException(String message, String debugMessage, Throwable clause,
                                 int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public HotelAvailabilityException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
