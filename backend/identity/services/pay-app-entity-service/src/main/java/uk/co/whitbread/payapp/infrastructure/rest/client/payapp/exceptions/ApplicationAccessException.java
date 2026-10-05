package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.payapp.ErrorCode;

public class ApplicationAccessException extends AbstractBadRequestException {

  public ApplicationAccessException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
