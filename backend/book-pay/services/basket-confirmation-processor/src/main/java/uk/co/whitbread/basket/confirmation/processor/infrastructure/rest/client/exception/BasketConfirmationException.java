package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.exception;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class BasketConfirmationException extends AbstractInternalException {

  public BasketConfirmationException(String message, String debugMessage, Throwable clause,
                                   int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}
