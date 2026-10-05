package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL400HttpException;

public class UnauthorizedULException extends AbstractMALException implements MAL400HttpException {

  public UnauthorizedULException(String message) {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return "7011";
  }
}
