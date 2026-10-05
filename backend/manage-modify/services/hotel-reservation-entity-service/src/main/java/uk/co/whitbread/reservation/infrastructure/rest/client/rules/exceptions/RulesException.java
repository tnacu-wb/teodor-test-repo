package uk.co.whitbread.reservation.infrastructure.rest.client.rules.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class RulesException extends AbstractInternalException {

  public RulesException(String message) {
    super(message, message, 0);
  }

  public RulesException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
