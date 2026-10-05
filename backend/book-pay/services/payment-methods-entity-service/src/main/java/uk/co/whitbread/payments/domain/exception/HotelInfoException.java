package uk.co.whitbread.payments.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelInfoException extends AbstractInternalException {
  public HotelInfoException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

  public HotelInfoException(String message, String debugMessage, Throwable clause,
                                   int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
