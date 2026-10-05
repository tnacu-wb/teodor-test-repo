package uk.co.whitbread.content.infrastructure.rest.client.content.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.content.domain.model.ErrorCode;

public class PromotionException extends AbstractInternalException {

  public PromotionException(ErrorCode error, String debugMessage) {
    super(
            error.getMessage(),
            debugMessage,
            error.getCode()
    );
  }
}
