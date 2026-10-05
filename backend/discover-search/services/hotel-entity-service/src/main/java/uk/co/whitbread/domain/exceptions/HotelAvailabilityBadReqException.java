package uk.co.whitbread.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class HotelAvailabilityBadReqException extends AbstractBadRequestException {

  public HotelAvailabilityBadReqException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}
