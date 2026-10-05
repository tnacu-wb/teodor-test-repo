package uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.content.domain.model.ErrorCode;

public class SnowdropResponseException extends AbstractInternalException {

  public SnowdropResponseException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }

}
