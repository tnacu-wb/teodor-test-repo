package uk.co.whitbread.payments.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class AccountsServiceException extends AbstractBadRequestException {

  public AccountsServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

  public AccountsServiceException(String message, String debugMessage, Throwable clause,
                          int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}