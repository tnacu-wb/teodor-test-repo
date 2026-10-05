package uk.co.whitbread.wallet.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.wallet.ErrorCode;

public class CertificateException extends AbstractInternalException {

  public CertificateException(ErrorCode error, String debugMessage,
      Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }

  public CertificateException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
