package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class RateCodePricingException extends AbstractInternalException {

  public RateCodePricingException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}