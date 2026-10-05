package uk.co.whitbread.infrastructure.rest.client.accounts.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class AccountServiceException extends AbstractInternalException {

  public AccountServiceException(String message, String debugMessage, Throwable clause,
                                 int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
