package uk.co.whitbread.company.employee.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL401HttpException;

public class InvalidTokenException extends AbstractMALException implements MAL401HttpException {

  public static final String ERROR_CODE = "2521";

  public InvalidTokenException(String message) {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
