package uk.co.whitbread.company.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class NegotiatedRateNotAvailableException extends AbstractNotFoundException {

  public NegotiatedRateNotAvailableException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
