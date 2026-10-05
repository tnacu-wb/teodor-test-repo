package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.reservation.domain.exceptions.ErrorCode;

public class HotelAvailabilityException extends AbstractInternalException {

  public HotelAvailabilityException(uk.co.whitbread.reservation.ErrorCode error,
                                    String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public HotelAvailabilityException(uk.co.whitbread.reservation.ErrorCode error,
                                    String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
