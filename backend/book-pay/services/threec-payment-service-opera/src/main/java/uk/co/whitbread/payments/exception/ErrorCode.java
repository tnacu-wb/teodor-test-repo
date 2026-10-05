package uk.co.whitbread.payments.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

  DIGITAL_THREEC_MIT_CC_PAYMENT_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 620),
  DIGITAL_THREEC_TIMEOUT_EXCEPTION(Constants.TIMEOUT_EXCEPTION, 621);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    public static final String TIMEOUT_EXCEPTION = "mit.cc.timeout.exception";

    private Constants() {
    }
  }
}
