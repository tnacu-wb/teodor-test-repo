package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBusinessValidationException;

public class PaymentFraudException extends AbstractBusinessValidationException {

  public PaymentFraudException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

}
