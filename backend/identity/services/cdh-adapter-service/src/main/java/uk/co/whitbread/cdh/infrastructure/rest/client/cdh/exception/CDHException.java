package uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class CDHException extends AbstractInternalException {

  public CDHException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
