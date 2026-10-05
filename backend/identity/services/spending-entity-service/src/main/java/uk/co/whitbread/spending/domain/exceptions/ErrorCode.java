package uk.co.whitbread.spending.domain.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  ACCOUNT_NOT_BELONGING_TO_USER(Constants.GENERIC_VALIDATION_EXCEPTION, 250),
  TOKEN_CANNOT_BE_PARSED(Constants.GENERIC_VALIDATION_EXCEPTION, 251);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String GENERIC_VALIDATION_EXCEPTION = "generic.validation.exception";

    private Constants() {
    }
  }
}
