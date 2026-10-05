package uk.co.whitbread.ohip.domain.utils;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SanitizingUtils {

  public static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";

  public static String sanitize(final Object input) {
    if (!Objects.isNull(input)) {
      try {
        return input.toString().replaceAll(CONTROL_CHARACTER_REGEX, "");
      } catch (Exception ex) {
        log.debug("Error sanitizing input: {}", ex.getMessage());
      }
    }
    return "";
  }

}
