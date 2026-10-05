package uk.co.whitbread.company.infrastructure.rest.client.company.exceptions;

import org.springframework.http.HttpStatus;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;
import uk.co.whitbread.company.infrastructure.exceptions.ErrorCode;

public class RecordsCountExceedException extends AbstractInternalException {

  public RecordsCountExceedException(String message) {
    super(message, ErrorCode.REPORT_LARGE_CONTENT.getCode(), HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
