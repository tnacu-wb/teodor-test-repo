package uk.co.whitbread.hotel.card.exceptions;

import uk.co.whitbread.common.exceptions.AbstractMALException;
import uk.co.whitbread.common.exceptions.http.MAL403HttpException;

public class UnauthorizedSaveCentralCardException extends AbstractMALException implements
    MAL403HttpException {

  private static final String DEFAULT_ERROR_CODE = "614";

  public UnauthorizedSaveCentralCardException(String message) {
    super(message);
  }

  @Override
  public String getErrorCode() {
    return DEFAULT_ERROR_CODE;
  }

}
