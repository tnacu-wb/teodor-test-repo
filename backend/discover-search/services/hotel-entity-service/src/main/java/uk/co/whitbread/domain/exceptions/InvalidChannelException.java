package uk.co.whitbread.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class InvalidChannelException extends AbstractBadRequestException {

  public InvalidChannelException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
