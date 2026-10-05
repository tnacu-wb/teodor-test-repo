package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;

public class CdhServiceException extends AbstractMALException {

  private static final String ERROR_CODE = "7009";

  public CdhServiceException(String message, Throwable e) {
    super(message, e);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
