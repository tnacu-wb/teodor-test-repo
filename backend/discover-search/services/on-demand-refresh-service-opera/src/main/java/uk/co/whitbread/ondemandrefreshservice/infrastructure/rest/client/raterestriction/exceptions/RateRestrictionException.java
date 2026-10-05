package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.raterestriction.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ondemandrefreshservice.ErrorCode;

public class RateRestrictionException extends AbstractInternalException {

  public RateRestrictionException(ErrorCode error, String debugMessage) {
    super(debugMessage, error.getMessage(), error.getCode());
  }
}