package uk.co.whitbread.piba.registration.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LogUtils {

  public static String sanitisedStringWithMaxLengthLimit(final String stringToBeSanitised,
      final int limit) {
    if (stringToBeSanitised == null) {
      return null;
    }

    String sanitisezString = stringToBeSanitised.replaceAll("(\r\n|\r|\n)", "");
    return sanitisezString.substring(0, Math.min(limit, sanitisezString.length()));
  }

}
