package uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;
import uk.co.whitbread.content.domain.model.ErrorCode;

public class ResourceNotFoundException extends AbstractNotFoundException {

  public ResourceNotFoundException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
