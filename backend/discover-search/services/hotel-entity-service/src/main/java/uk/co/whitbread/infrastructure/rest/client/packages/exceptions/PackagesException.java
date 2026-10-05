package uk.co.whitbread.infrastructure.rest.client.packages.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class PackagesException extends AbstractInternalException {

  public PackagesException(String message, String debugMessage, Throwable clause,
                             int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  public PackagesException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
