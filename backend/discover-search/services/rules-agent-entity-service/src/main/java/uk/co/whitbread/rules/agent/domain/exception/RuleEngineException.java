package uk.co.whitbread.rules.agent.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class RuleEngineException extends AbstractNotFoundException {

  public RuleEngineException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

}
