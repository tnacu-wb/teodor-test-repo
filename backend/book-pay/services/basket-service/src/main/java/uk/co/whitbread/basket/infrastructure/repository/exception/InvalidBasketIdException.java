package uk.co.whitbread.basket.infrastructure.repository.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class InvalidBasketIdException extends AbstractBadRequestException {

  public InvalidBasketIdException(uk.co.whitbread.basket.domain.exception.ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
