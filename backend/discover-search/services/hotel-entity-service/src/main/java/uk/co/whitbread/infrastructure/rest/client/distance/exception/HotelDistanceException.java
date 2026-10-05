package uk.co.whitbread.infrastructure.rest.client.distance.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class HotelDistanceException extends AbstractInternalException {

  public HotelDistanceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}

