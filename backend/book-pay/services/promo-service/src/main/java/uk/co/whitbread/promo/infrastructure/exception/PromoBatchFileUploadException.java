package uk.co.whitbread.promo.infrastructure.exception;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;

public class PromoBatchFileUploadException extends AbstractNotFoundException {

  public PromoBatchFileUploadException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }
}
