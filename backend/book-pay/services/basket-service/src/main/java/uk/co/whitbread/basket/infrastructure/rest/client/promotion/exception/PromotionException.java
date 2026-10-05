package uk.co.whitbread.basket.infrastructure.rest.client.promotion.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PromotionException extends AbstractInternalException {

  public PromotionException(String message, String debugMessage,
      Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
