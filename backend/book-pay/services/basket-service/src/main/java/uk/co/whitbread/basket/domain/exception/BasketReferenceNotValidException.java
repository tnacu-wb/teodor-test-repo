package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class BasketReferenceNotValidException extends AbstractBadRequestException {
  public BasketReferenceNotValidException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}