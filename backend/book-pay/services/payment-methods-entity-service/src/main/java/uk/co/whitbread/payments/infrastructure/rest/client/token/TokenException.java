package uk.co.whitbread.payments.infrastructure.rest.client.token;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.payments.domain.exception.ErrorCode;

public class TokenException extends AbstractInternalException {

  public TokenException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, null, error.getCode());
  }

}
