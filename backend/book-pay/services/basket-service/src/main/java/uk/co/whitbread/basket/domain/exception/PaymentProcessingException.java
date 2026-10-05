package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PaymentProcessingException extends AbstractInternalException {

  public PaymentProcessingException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
