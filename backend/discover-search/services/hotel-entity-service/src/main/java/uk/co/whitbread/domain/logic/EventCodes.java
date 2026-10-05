package uk.co.whitbread.domain.logic;

import java.util.Arrays;

public enum EventCodes {
  BDAY,
  ANNV,
  WEDD,
  GRAD,
  JOBS,
  FCEL,
  SPRT;

  public static boolean isValidEventCode(String code) {
    return Arrays.stream(EventCodes.values())
        .map(Enum::name)
        .anyMatch(enumValue -> enumValue.equals(code));
  }
}
