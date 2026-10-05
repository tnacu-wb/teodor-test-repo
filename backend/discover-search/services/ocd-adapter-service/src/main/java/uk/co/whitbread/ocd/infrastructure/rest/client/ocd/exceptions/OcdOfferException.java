package uk.co.whitbread.ocd.infrastructure.rest.client.ocd.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.ocd.infrastructure.rest.exception.ErrorCode;

public class OcdOfferException extends AbstractInternalException {

  public OcdOfferException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

  public OcdOfferException(ErrorCode error, String debugMessage, Throwable cause) {
    super(error.getMessage(), debugMessage, cause, error.getCode());
  }
}
