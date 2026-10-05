package uk.co.whitbread.spending.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongTokenException extends AbstractBadRequestException {

  public WrongTokenException(ErrorCode errorCode, String debugMessage) {
    super(errorCode.getMessage(), debugMessage, null, errorCode.getCode());
  }
}
