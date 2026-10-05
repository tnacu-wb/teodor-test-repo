package uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.reservation.ErrorCode;

public class BasketDigitalException extends AbstractInternalException {
  public BasketDigitalException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
