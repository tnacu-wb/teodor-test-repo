package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.digitalkey.ErrorCode;

public class ResourceNotFoundException extends AbstractInternalException {

  public ResourceNotFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
