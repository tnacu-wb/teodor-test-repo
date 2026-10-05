package uk.co.whitbread.booking.infrastructure.rest.client.cdh.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class CdhEmptyResponseException extends AbstractNotFoundException {

  public CdhEmptyResponseException(String message, String debugMessage, int errorCode) {
    super(message, debugMessage, errorCode);
  }
}
