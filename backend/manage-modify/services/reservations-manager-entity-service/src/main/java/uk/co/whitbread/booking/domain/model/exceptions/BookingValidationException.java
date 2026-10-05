package uk.co.whitbread.booking.domain.model.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class BookingValidationException extends AbstractBadRequestException {

  public BookingValidationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}

