package uk.co.whitbread.dashboard.infrastructure.rest.client.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelAccountException extends AbstractInternalException {

  public HotelAccountException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}