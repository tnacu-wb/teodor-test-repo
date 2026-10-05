package uk.co.whitbread.hotelcountries.util;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Utils {

  private Utils() {
    /* This utility class should not be instantiated */
  }

  private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";

  public static String sanitizeInputString(String input) {
    if (!Objects.isNull(input)) {
      try {
        return input.replaceAll(CONTROL_CHARACTER_REGEX, "").trim();
      } catch (Exception ex) {
        log.debug("Error sanitizing input: {}", ex.getMessage());
      }
    }
    return "";
  }
}
