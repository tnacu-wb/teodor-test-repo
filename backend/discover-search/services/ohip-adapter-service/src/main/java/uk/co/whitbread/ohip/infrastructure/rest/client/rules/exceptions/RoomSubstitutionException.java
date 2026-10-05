package uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class RoomSubstitutionException extends AbstractInternalException {

  @JsonCreator
  public RoomSubstitutionException(
      @JsonProperty("message") String message,
      @JsonProperty("debugMessage") String debugMessage,
      @JsonProperty("errorCode") int errCode) {
    super(message, debugMessage, errCode);
  }

  public RoomSubstitutionException(String message, String debugMessage, Throwable clause,
      int errCode) {
    super(message, debugMessage, clause, errCode);
  }
}
