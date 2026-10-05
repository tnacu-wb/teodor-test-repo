package uk.co.whitbread.ohip.infrastructure.rest.client.rates.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class RatePlansException extends AbstractInternalException {

  public RatePlansException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}