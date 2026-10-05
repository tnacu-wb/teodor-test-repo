package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.exceptions;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class DiscountInvalidAmountException extends AbstractBadRequestException {

  public DiscountInvalidAmountException(String message) {
    super(message, message, 0);
  }

  public DiscountInvalidAmountException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
