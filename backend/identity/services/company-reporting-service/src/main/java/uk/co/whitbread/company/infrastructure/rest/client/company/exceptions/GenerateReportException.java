package uk.co.whitbread.company.infrastructure.rest.client.company.exceptions;


import org.springframework.http.HttpStatus;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractNotFoundException;
import uk.co.whitbread.company.infrastructure.exceptions.ErrorCode;

public class GenerateReportException extends AbstractNotFoundException {

  public GenerateReportException(String message) {
    super(message, ErrorCode.REPORT_NOT_FOUND.getCode(), HttpStatus.NOT_FOUND.value());
  }

}
