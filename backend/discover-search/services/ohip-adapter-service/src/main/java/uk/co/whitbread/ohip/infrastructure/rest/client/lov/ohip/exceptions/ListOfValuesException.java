package uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ohip.ErrorCode;

public class ListOfValuesException extends AbstractInternalException {

  public ListOfValuesException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
