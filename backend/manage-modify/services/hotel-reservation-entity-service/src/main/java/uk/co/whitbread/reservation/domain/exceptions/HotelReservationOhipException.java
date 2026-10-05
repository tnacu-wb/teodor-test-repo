package uk.co.whitbread.reservation.domain.exceptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class HotelReservationOhipException extends AbstractInternalException {

  public HotelReservationOhipException(String message) {
    super(message, message, 0);
  }

  public HotelReservationOhipException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }

  @JsonCreator
  public HotelReservationOhipException(@JsonProperty("globalErrTextTemplate") String message,
                                       @JsonProperty("debugMessage") String debugMessage,
                                       @JsonProperty("errCode") int errCode) {
    super(message, debugMessage, null, errCode);
  }
}