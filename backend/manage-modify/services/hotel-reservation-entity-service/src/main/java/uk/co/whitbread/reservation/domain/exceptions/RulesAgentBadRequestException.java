package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.reservation.ErrorCode;

public class RulesAgentBadRequestException extends AbstractBadRequestException {

  public RulesAgentBadRequestException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
