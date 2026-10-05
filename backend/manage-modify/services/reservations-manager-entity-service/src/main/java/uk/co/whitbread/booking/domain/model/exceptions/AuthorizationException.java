package uk.co.whitbread.booking.domain.model.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class AuthorizationException extends AbstractBadRequestException {

  public AuthorizationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage,  error.getCode());
  }
}
