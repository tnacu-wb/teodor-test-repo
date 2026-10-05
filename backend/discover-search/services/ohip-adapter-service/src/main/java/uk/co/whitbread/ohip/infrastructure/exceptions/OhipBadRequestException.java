package uk.co.whitbread.ohip.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.ohip.ErrorCode;

public class OhipBadRequestException extends AbstractBadRequestException {

  public OhipBadRequestException(String debugMessage, String globalErrTextTemplate, int errorCode) {
    super(debugMessage, globalErrTextTemplate, errorCode);
  }

  public OhipBadRequestException(String debugMessage, String globalErrTextTemplate, Throwable cause,
      int errorCode) {
    super(debugMessage, globalErrTextTemplate, cause, errorCode);
  }

  public OhipBadRequestException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }
}
