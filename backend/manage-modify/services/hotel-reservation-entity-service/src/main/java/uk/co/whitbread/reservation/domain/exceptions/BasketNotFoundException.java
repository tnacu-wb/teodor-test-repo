package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class BasketNotFoundException extends AbstractNotFoundException {

  public BasketNotFoundException(String message) {
    super(message, message, 0);
  }

  public BasketNotFoundException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
