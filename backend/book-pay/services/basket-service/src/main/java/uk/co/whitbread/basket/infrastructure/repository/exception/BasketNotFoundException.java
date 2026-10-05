package uk.co.whitbread.basket.infrastructure.repository.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class BasketNotFoundException extends AbstractNotFoundException {

  public BasketNotFoundException(uk.co.whitbread.basket.domain.exception.ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
