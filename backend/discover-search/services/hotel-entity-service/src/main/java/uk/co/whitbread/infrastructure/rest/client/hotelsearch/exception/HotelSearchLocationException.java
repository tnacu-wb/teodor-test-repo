package uk.co.whitbread.infrastructure.rest.client.hotelsearch.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class HotelSearchLocationException extends AbstractBadRequestException {

  public HotelSearchLocationException(String message, String debugMessage, Throwable clause,
                                     int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public HotelSearchLocationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
