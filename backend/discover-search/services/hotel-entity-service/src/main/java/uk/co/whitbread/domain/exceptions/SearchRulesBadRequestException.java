package uk.co.whitbread.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class SearchRulesBadRequestException extends AbstractBadRequestException {

  public SearchRulesBadRequestException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
