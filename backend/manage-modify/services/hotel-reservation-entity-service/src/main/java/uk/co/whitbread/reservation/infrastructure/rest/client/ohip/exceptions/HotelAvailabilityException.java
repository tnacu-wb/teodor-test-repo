package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.reservation.domain.exceptions.ErrorCode;

public class HotelAvailabilityException extends AbstractInternalException {

  public HotelAvailabilityException(String message) {

    super(message, ErrorCode.GENERIC_EXCEPTION.getCode(), 0);
  }

  public HotelAvailabilityException(String message, Throwable cause) {
    super(message, ErrorCode.GENERIC_EXCEPTION.getCode(), 0);
  }

  public HotelAvailabilityException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}
