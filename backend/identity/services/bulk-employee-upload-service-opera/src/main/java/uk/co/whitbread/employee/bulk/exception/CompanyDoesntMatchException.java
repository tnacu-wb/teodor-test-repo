package uk.co.whitbread.employee.bulk.exception;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

public class CompanyDoesntMatchException extends AbstractMALException implements
        MAL401HttpException {

  public static final String ERROR_CODE = "2517";

  public CompanyDoesntMatchException(String message) {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
