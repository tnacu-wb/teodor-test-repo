package uk.co.whitbread.promo.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.co.whitbread.commons.exceptions.exception.generic.AbstractInternalException;

public class OhipException extends AbstractInternalException {

  public OhipException(String message, String debugMessage, Throwable clause, int errorCode) {
    super(message, debugMessage, clause, errorCode);
  }

  @JsonCreator
  public OhipException(
          @JsonProperty("globalErrTextTemplate") String message,
          @JsonProperty("debugMessage") String debugMessage,
          @JsonProperty("errCode") int errorCode) {

    super(message, debugMessage, null, errorCode);
  }

}