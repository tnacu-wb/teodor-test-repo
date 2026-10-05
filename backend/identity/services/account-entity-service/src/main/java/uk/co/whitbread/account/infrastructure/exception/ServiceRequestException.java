package uk.co.whitbread.account.infrastructure.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class ServiceRequestException extends AbstractBadRequestException {

  public ServiceRequestException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
