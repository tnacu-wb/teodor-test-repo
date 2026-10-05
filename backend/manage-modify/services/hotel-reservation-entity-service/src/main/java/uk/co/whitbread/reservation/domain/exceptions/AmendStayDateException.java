package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class AmendStayDateException extends AbstractBadRequestException {

  public AmendStayDateException(uk.co.whitbread.reservation.ErrorCode error, String debugMessage,
      Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}