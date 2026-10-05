package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL500HttpException;

public class WorldlineServiceException extends AbstractMALException implements MAL500HttpException {

  private static final String ERROR_CODE = "7102";

  public WorldlineServiceException(String message) {
    super(message);
  }

  public WorldlineServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}

