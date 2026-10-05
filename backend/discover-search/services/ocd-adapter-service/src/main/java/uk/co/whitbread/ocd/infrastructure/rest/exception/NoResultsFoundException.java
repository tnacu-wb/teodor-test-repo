package uk.co.whitbread.ocd.infrastructure.rest.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class NoResultsFoundException extends AbstractNotFoundException {
  public NoResultsFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}