package uk.co.whitbread.wallet.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.wallet.ErrorCode;

public class SignatureException extends AbstractInternalException {

  public SignatureException(ErrorCode errorCode, String debugMessage, Exception e) {
    super(errorCode.getMessage(), debugMessage, e, errorCode.getCode());
  }
}
