package uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class BasketDigitalNotFoundException extends AbstractNotFoundException {
  public BasketDigitalNotFoundException(uk.co.whitbread.reservation.ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
