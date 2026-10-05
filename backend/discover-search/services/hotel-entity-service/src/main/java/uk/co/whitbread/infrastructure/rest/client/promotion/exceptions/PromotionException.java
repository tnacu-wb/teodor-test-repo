package uk.co.whitbread.infrastructure.rest.client.promotion.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class PromotionException extends AbstractInternalException {

  public PromotionException(String message, String debugMessage,
      Throwable clause, int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
