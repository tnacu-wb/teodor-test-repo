package uk.co.whitbread.infrastructure.rest.client.hotelsearch.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelSearchGenericException extends AbstractInternalException {

  public HotelSearchGenericException(String message, String debugMessage, Throwable clause,
                                     int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}


