package uk.co.whitbread.content.domain.model.seo.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.content.domain.model.ErrorCode;

public class MissingRequiredFieldException extends AbstractBadRequestException {

  public MissingRequiredFieldException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
