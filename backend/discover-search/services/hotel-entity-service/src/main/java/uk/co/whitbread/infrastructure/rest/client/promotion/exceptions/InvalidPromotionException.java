package uk.co.whitbread.infrastructure.rest.client.promotion.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.domain.exceptions.ErrorCode;

public class InvalidPromotionException extends AbstractBadRequestException {

  public InvalidPromotionException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
