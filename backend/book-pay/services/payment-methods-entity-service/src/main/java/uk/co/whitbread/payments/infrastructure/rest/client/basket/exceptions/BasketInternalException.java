package uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class BasketInternalException extends AbstractInternalException {


  public BasketInternalException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
