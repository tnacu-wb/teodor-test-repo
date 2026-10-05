package uk.co.whitbread.ohip.infrastructure.rest.client.packages.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class PackagesException extends AbstractInternalException {

  public PackagesException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}