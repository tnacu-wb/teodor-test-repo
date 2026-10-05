package uk.co.whitbread.hotel.account.exceptions;

import uk.co.whitbread.common.exceptions.http.MALHttpException;

public interface MAL413HttpException extends MALHttpException {
  default int getStatus() {
    return 413;
  }
}