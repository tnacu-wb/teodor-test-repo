package uk.co.whitbread.company.infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class AuthorizationException extends AbstractBadRequestException {

  public AuthorizationException(String debugMessage) {
    super(debugMessage, ErrorCode.UNAUTHORIZED_EXCEPTION.getCode(), HttpStatus.UNAUTHORIZED.value());
  }
}
