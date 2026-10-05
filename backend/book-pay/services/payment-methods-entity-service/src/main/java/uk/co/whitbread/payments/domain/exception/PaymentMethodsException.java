package uk.co.whitbread.payments.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PaymentMethodsException extends AbstractInternalException {

  public PaymentMethodsException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

  public PaymentMethodsException(String message, String debugMessage, Throwable clause,
                            int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}