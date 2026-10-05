package uk.co.whitbread.reservation.infrastructure.rest.client.content.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class NoSearchRulesDataException extends AbstractNotFoundException {

  public NoSearchRulesDataException(String message) {
    super(message, message, 0);
  }

  public NoSearchRulesDataException(String message, String debugMessage, Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
