package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBusinessValidationException;

/**
 * Exception for payment business validation errors that should return HTTP 409 Conflict.
 * Used for 3CP return codes 614, 615, 616 which represent known business outcomes
 * rather than internal server failures.
 */
public class PaymentBusinessException extends AbstractBusinessValidationException {

  public PaymentBusinessException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

  public PaymentBusinessException(String message, String debugMessage, int errCode) {
    super(message, debugMessage, null, errCode);
  }
}
