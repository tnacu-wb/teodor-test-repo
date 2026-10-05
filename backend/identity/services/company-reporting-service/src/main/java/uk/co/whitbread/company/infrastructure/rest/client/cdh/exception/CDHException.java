package uk.co.whitbread.company.infrastructure.rest.client.cdh.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.company.infrastructure.exceptions.ErrorCode;

@Getter
@Setter
public class CDHException extends AbstractInternalException {

  public CDHException(String message) {
    super(message, ErrorCode.INTERNAL_SERVER_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  public CDHException(String message, HttpStatusCode httpStatusCode) {
    super(message, String.valueOf(httpStatusCode), httpStatusCode.value());
  }
}
