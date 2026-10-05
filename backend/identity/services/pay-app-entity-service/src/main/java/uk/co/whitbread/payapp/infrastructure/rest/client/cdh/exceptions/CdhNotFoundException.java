package uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;
import uk.co.whitbread.payapp.ErrorCode;

public class CdhNotFoundException extends AbstractNotFoundException {

  public CdhNotFoundException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }
}
