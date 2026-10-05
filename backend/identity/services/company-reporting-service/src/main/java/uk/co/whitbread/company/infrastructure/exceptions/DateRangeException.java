package uk.co.whitbread.company.infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractBadRequestException;

public class DateRangeException extends AbstractBadRequestException {

  public DateRangeException(String message) {
    super(message, ErrorCode.DATE_RANGE_EXCEPTION.getCode(), HttpStatus.BAD_REQUEST.value());
  }

}
