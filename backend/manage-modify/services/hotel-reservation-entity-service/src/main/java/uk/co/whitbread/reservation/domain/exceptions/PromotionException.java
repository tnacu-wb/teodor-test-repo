package uk.co.whitbread.reservation.domain.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.reservation.ErrorCode;

public class PromotionException extends AbstractInternalException {

  public PromotionException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }
}
