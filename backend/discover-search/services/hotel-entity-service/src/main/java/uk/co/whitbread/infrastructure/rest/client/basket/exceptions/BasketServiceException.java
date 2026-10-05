package uk.co.whitbread.infrastructure.rest.client.basket.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class BasketServiceException  extends AbstractInternalException {

  public BasketServiceException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}