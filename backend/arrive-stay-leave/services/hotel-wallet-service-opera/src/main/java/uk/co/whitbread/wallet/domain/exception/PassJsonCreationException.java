package uk.co.whitbread.wallet.domain.exception;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.wallet.ErrorCode;

public class PassJsonCreationException extends AbstractInternalException {

  public PassJsonCreationException(ErrorCode error, String debugMessage,
      Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}