package uk.co.whitbread.account.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  UPDATE_MARKETING_PREFERENCES_REQUEST_INCORRECT_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 538),
  UPDATE_MARKETING_PREFERENCES_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 539),
  REGISTER_CUSTOMER_REQUEST_INCORRECT_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 540),
  REGISTER_CUSTOMER_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 541),
  UPDATE_MARKETING_PREFERENCES_REQUEST_INCORRECT_EXCEPTION_V2(Constants.INTERNAL_SERVER_EXCEPTION, 538),
  UPDATE_MARKETING_PREFERENCES_EXCEPTION_V2(Constants.INTERNAL_SERVER_EXCEPTION, 539);


  private final String message;
  private final int code;

  public static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";

    private Constants() {}
  }
}
