package uk.co.whitbread.basket.infrastructure.queue.exception;

import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class BasketOrderException extends AbstractBadRequestException {

  public BasketOrderException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
