package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.MALException;

public class PibaAccountServiceException extends AbstractMALException implements MALException {

  private static final String ERROR_CODE = "7103";

  public PibaAccountServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}

