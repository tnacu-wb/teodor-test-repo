package uk.co.whitbread.rules.manager.infrastructure.exceptions;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  DIGITAL_CREATE_DB_EXCEPTION(Constants.INTERNAL_SERVER_EXCEPTION, 219);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";

    private Constants() {}
  }
}
