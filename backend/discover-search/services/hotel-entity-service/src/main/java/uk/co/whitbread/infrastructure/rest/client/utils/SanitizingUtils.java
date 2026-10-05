package uk.co.whitbread.infrastructure.rest.client.utils;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SanitizingUtils {

  private static final String CHARACTER_REGEX = "[\\r\\n]";
  private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
  private static final String SAFE_CHAR_PATTERN = "[^a-zA-Z0-9-:(),=_/'!@#$%&*?{}]";

  public static String sanitize(final Object input) {
    if (!Objects.isNull(input)) {
      try {
        return input.toString().replaceAll(CHARACTER_REGEX, "")
            .replaceAll(CONTROL_CHARACTER_REGEX, "")
            .replaceAll(SAFE_CHAR_PATTERN, "");
      } catch (Exception ex) {
        log.debug("Error sanitizing input: {}", ex.getMessage());
      }
    }
    return "";
  }

  public static String sanitizeForLog(final Object input) {
    if (Objects.isNull(input)) {
      return "[USER:null]";
    }
    return "[USER:" + sanitize(input) + "]";
  }
}
