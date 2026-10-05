package uk.co.whitbread.shared.auth.config.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  UNAUTHORIZED_EXCEPTION(Constants.UNAUTHORIZED_EXCEPTION, 413),
  AUTH0_RETRIES_EXHAUSTED_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 975);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    public static final String UNAUTHORIZED_EXCEPTION = "unauthorized.exception";

    private Constants() {
    }
  }
}
