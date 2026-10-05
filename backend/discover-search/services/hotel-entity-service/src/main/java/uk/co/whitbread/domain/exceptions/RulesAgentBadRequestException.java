package uk.co.whitbread.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class RulesAgentBadRequestException extends AbstractBadRequestException {

  public RulesAgentBadRequestException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
