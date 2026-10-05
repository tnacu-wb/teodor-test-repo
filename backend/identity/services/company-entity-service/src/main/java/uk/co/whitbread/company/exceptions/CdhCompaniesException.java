package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;


public class CdhCompaniesException extends AbstractInternalException {

  public CdhCompaniesException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
