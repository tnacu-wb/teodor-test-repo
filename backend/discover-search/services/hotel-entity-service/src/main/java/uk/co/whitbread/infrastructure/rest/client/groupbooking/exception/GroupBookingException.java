package uk.co.whitbread.infrastructure.rest.client.groupbooking.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class GroupBookingException extends AbstractInternalException {

  public GroupBookingException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public GroupBookingException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
