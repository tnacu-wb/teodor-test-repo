package uk.co.whitbread.promo.infrastructure.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class PromoBatchException extends AbstractNotFoundException {

  public PromoBatchException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
