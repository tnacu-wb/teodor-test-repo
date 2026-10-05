package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class PaymentInvalidException extends AbstractBadRequestException {

  public PaymentInvalidException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
