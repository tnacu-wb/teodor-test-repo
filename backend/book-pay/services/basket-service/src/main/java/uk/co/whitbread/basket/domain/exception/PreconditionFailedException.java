package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class PreconditionFailedException extends AbstractBadRequestException {

  public PreconditionFailedException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
