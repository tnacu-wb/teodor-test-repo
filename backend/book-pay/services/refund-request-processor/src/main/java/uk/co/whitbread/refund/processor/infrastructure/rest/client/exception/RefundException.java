package uk.co.whitbread.refund.processor.infrastructure.rest.client.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class RefundException extends AbstractInternalException {

  public RefundException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }

}
