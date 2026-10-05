package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class CompanyNotFoundException extends AbstractNotFoundException {

  public CompanyNotFoundException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
