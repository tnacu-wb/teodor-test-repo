package uk.co.whitbread.payapp.infrastructure.rest.client.company.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.payapp.ErrorCode;

public class CompanyResponseException extends AbstractInternalException {

  public CompanyResponseException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }

}
