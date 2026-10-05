package uk.co.whitbread.payments.domain.exception;

import java.util.Map;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBusinessValidationException;

public class PaymentMethodsValidationException extends AbstractBusinessValidationException {

  public PaymentMethodsValidationException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
