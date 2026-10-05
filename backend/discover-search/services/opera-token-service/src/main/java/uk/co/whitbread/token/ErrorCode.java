package uk.co.whitbread.token;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  TOKEN_UNABLE_TO_ACQUIRE_TOKEN_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 974),
  TOKEN_NO_SUCH_PROVIDER_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 975);

  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
  }
}