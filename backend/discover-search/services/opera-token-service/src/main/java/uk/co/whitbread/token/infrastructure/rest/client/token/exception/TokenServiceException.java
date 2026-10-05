package uk.co.whitbread.token.infrastructure.rest.client.token.exception;

import java.io.Serial;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.token.ErrorCode;

public class TokenServiceException extends AbstractInternalException {

  @Serial
  private static final long serialVersionUID = 1L;

  public TokenServiceException(ErrorCode error, String debugMessage) {
    super(error.getMessage(), debugMessage, error.getCode());
  }

}
