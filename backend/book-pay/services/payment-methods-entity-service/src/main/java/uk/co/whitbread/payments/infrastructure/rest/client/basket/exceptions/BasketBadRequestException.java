package uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class BasketBadRequestException extends AbstractBadRequestException {
  public BasketBadRequestException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
