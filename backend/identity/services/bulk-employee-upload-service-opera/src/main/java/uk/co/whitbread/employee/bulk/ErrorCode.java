package uk.co.whitbread.employee.bulk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Errors map.The Error's message is used by the globalTxtErrTemplate.
 */

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
  EMPLOYEE_EXISTS(Constants.INTERNAL_SERVER_EXCEPTION, 282),
  EMAIL_IS_EMPTY(Constants.GENERIC_VALIDATION_EXCEPTION, 283),
  EMAIL_IS_INVALID(Constants.GENERIC_VALIDATION_EXCEPTION, 284),
  TITLE_EMPTY(Constants.GENERIC_VALIDATION_EXCEPTION, 285),
  FIRST_NAME_EMPTY(Constants.GENERIC_VALIDATION_EXCEPTION, 286),
  LAST_NAME_EMPTY(Constants.GENERIC_VALIDATION_EXCEPTION, 287),
  TEXT_CONFIRMATION_EMPTY(Constants.GENERIC_VALIDATION_EXCEPTION, 288),
  ACCESS_LEVEL_EMPTY(Constants.GENERIC_VALIDATION_EXCEPTION, 289),
  FILE_PARTIAL_PROCESSED(Constants.GENERIC_VALIDATION_EXCEPTION, 293);


  private final String message;
  private final int code;

  private static class Constants {

    public static final String INTERNAL_SERVER_EXCEPTION = "internal.server.exception";
    public static final String GENERIC_VALIDATION_EXCEPTION = "generic.validation.exception";
  }
}