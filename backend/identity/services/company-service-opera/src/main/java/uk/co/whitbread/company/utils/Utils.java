package uk.co.whitbread.company.utils;

import java.util.Objects;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class Utils {

  private static final String CONTROL_CHARACTER_REGEX = "[\\p{Cntrl}&&[^\r\n\t]]";

  public static String sanitizeInputString(String input) {
    if (!Objects.isNull(input)) {
      try {
        return input.replaceAll(CONTROL_CHARACTER_REGEX, "")
            .replaceAll("[\r\n]", "");
      } catch (Exception ex) {
        log.debug("Error sanitizing input: {}", ex.getMessage());
      }
    }
    return "";
  }

}
