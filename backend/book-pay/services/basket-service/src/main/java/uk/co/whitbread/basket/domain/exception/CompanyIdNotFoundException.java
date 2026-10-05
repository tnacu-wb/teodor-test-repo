package uk.co.whitbread.basket.domain.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class CompanyIdNotFoundException extends AbstractBadRequestException {

  public CompanyIdNotFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
