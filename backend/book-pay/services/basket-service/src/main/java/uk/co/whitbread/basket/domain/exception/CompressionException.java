package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class CompressionException extends AbstractInternalException {

  public CompressionException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
