package uk.co.whitbread.payments.infrastructure.rest.client.reservation;


import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelReservationException extends AbstractInternalException {
  public HotelReservationException(String message, String debugMessage, Throwable clause,
                                   int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}