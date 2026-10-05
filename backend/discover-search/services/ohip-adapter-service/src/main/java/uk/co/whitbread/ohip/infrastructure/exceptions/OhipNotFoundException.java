package uk.co.whitbread.ohip.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class OhipNotFoundException extends AbstractNotFoundException {

  public OhipNotFoundException(String debugMessage, String globalErrTextTemplate, int errorCode) {
    super(debugMessage, globalErrTextTemplate, errorCode);
  }

  public OhipNotFoundException(String debugMessage, String globalErrTextTemplate, Throwable cause,
      int errorCode) {
    super(debugMessage, globalErrTextTemplate, cause, errorCode);
  }
}
