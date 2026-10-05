package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class CompanyServiceException extends AbstractInternalException {

  public CompanyServiceException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}
