package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBusinessValidationException;

public class CardDetailsDeclinedException extends AbstractBusinessValidationException {

  public CardDetailsDeclinedException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
