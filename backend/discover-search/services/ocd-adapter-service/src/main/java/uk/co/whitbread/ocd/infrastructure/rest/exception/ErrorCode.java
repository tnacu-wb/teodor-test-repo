package uk.co.whitbread.ocd.infrastructure.rest.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  OCD_OFFER_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 1);

  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
  }
}