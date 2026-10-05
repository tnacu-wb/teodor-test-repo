package uk.co.whitbread.availabilitycacheservice.domain.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SanitizingUtils {

  private static final Pattern SAFE_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9\\-:(),.=_/'!@#$%&amp;*?{}]");
  private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("[\\p{Cntrl}]");

  public static String sanitize(final Object input) {
    if (!Objects.isNull(input)) {
      try {
        String inputString = input.toString();
        String sanitized = SAFE_CHAR_PATTERN.matcher(inputString).replaceAll("");
        sanitized = CONTROL_CHAR_PATTERN.matcher(sanitized).replaceAll("");
        return sanitized;
      } catch (Exception ex) {
        log.debug("Error sanitizing input: {}", ex.getMessage());
      }
    }
    return "";
  }

  // Sanitizes roomTypes for safe logging: removes newlines and control chars, concatenates flat
  public static String sanitizeRoomTypesForLog(String[][] roomTypes) {
    if (roomTypes == null) {
      return "";
    }
    return Arrays.stream(roomTypes)
        .flatMap(Arrays::stream)
        .map(SanitizingUtils::sanitize)
        .collect(Collectors.joining(","));
  }
}
