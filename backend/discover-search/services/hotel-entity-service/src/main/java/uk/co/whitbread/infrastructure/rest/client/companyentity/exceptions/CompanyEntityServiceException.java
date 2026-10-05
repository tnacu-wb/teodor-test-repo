package uk.co.whitbread.infrastructure.rest.client.companyentity.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class CompanyEntityServiceException extends AbstractInternalException {

  public CompanyEntityServiceException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public CompanyEntityServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}

