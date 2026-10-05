package uk.co.whitbread.payments.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PaymentProcessingException extends AbstractInternalException {

  public PaymentProcessingException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

}
