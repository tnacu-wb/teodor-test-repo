package uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.payapp.ErrorCode;

public class EmployeeNotInCompanyException extends AbstractBadRequestException {

  public EmployeeNotInCompanyException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }
}
