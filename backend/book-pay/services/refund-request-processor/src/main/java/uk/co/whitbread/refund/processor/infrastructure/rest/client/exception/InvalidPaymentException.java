package uk.co.whitbread.refund.processor.infrastructure.rest.client.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class InvalidPaymentException extends AbstractBadRequestException {

  public InvalidPaymentException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }
}