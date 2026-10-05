package uk.co.whitbread.basket.infrastructure.rest.client.rules.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class RulesAgentException extends AbstractInternalException {

  public RulesAgentException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
