package uk.co.whitbread.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class NoResultsFoundException extends AbstractNotFoundException {
  public NoResultsFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
