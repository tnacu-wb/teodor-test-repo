package uk.co.whitbread.rules.manager.infrastructure.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class RuleEngineException extends AbstractInternalException {

  public RuleEngineException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

}
