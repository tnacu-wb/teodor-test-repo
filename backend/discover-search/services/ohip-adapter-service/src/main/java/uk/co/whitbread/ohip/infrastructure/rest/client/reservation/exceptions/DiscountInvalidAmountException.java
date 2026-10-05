package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.ohip.ErrorCode;

public class DiscountInvalidAmountException extends AbstractBadRequestException {

  public DiscountInvalidAmountException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}