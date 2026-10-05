package uk.co.whitbread.ohip.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OhipInternalException extends AbstractInternalException {

  public OhipInternalException(String debugMessage, String globalErrTextTemplate, int errorCode) {
    super(debugMessage, globalErrTextTemplate, errorCode);
  }

  public OhipInternalException(String debugMessage, String globalErrTextTemplate, Throwable cause,
      int errorCode) {
    super(debugMessage, globalErrTextTemplate, cause, errorCode);
  }
}
