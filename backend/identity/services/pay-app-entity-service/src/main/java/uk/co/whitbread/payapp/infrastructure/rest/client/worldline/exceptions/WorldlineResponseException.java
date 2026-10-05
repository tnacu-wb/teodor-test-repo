package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.payapp.ErrorCode;

public class WorldlineResponseException extends AbstractInternalException {

  public WorldlineResponseException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, errorCode.getCode());
  }
}
