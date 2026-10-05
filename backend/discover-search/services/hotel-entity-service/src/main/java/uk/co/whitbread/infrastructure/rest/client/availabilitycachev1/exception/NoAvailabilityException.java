package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class NoAvailabilityException extends AbstractInternalException {

  public NoAvailabilityException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

}
