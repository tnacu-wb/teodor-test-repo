package uk.co.whitbread.kiosk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  KIOSK_NO_ROOMS_AVAILABLE_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 244),
  KIOSK_PARTIAL_PAID_CHECK_IN_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 245),
  KIOSK_CARD_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 246),
  KIOSK_OUTSTANDING_BALANCE_NOT_PAID_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 247);

  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
  }
}