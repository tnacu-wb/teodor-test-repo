package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.digitalkey.ErrorCode;


public class AxpServiceException extends AbstractInternalException {
  public AxpServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
