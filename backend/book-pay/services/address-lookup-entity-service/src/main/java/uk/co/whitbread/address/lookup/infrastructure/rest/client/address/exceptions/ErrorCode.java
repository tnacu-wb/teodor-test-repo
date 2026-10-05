package uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  DIGITAL_INVALID_MONIKER_ID(Constants.INVALID_MONIKER_ID, 525),
  DIGITAL_QAS_SERVICE(Constants.QAS_SERVICE, 526),
  DIGITAL_QAS_SEARCH_SERVICE(Constants.QAS_SERVICE, 527);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String QAS_SERVICE = "qas.service";
    public static final String INVALID_MONIKER_ID = "invalid.moniker.id";

    private Constants() {
    }
  }
}