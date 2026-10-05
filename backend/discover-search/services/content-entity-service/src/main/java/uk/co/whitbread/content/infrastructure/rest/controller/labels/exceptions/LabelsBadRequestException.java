package uk.co.whitbread.content.infrastructure.rest.controller.labels.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.content.domain.model.ErrorCode;

public class LabelsBadRequestException extends AbstractBadRequestException {

  public LabelsBadRequestException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
