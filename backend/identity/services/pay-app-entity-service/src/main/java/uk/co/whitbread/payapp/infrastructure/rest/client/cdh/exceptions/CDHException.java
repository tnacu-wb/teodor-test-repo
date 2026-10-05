package uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.payapp.ErrorCode;


public class CDHException extends AbstractInternalException {

  public CDHException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
