package uk.co.whitbread.basket.infrastructure.rest.client.reservation.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelReservationException extends AbstractInternalException {

  public HotelReservationException() {
    super("", "", 0);
  }

  public HotelReservationException(String message) {
    super(message, message, 0);
  }

  public HotelReservationException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

}