package uk.co.whitbread.reservation.infrastructure.rest.client.packages.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PackagesException extends AbstractInternalException {

  public PackagesException(String message) {
    super(message, message, 0);
  }

  public PackagesException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
