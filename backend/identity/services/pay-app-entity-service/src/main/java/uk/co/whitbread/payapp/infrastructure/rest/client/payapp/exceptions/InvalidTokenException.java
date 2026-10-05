package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;
import uk.co.whitbread.payapp.ErrorCode;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTokenException extends AbstractBadRequestException {

  public InvalidTokenException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, null, errorCode.getCode());
  }
}