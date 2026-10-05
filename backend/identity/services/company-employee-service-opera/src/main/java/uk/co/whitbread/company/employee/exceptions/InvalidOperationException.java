package uk.co.whitbread.company.employee.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class InvalidOperationException extends AbstractMALException implements MAL400HttpException {

  public static final String ERROR_CODE = "2520";

  public InvalidOperationException(String message)  {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
