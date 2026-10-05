package uk.co.whitbread.basket.infrastructure.repository.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class BasketInternalException extends AbstractInternalException {

  public BasketInternalException(uk.co.whitbread.basket.domain.exception.ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
