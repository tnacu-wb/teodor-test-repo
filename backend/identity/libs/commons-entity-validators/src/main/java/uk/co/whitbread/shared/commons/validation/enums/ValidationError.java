package uk.co.whitbread.shared.commons.validation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ValidationError {

  DIGITAL_INVALID_COMPANY_NAME_EXCEPTION(Constants.GENERIC_VALIDATION_EXCEPTION, 414);

  private final String message;
  private final int code;

  public static class Constants {

    public static final String GENERIC_VALIDATION_EXCEPTION = "generic.validation.exception";

    private Constants() {
    }
  }
}
