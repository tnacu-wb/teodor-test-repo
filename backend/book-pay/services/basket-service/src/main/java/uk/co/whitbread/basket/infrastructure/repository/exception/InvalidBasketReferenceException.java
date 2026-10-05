package uk.co.whitbread.basket.infrastructure.repository.exception;

import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class InvalidBasketReferenceException extends AbstractBadRequestException {

  public InvalidBasketReferenceException(ErrorCode error,
      String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
