package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class CheckInRequestException extends AbstractBadRequestException {

  public CheckInRequestException(uk.co.whitbread.digitalkey.ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
