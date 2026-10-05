package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.exceptions;

import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class CdhReservationException extends AbstractInternalException {

  public CdhReservationException(String message) {
    super(message, message, 0);
  }

  public CdhReservationException(String message, String debugMessage, Throwable clause,
      int errorCode) {
    super(message, debugMessage, clause, errorCode);
  }
}
