package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class TooManyResultsException extends AbstractMALException implements MAL413HttpException {

  private static final String ERROR_CODE = "7010";

  public TooManyResultsException(String message) {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
