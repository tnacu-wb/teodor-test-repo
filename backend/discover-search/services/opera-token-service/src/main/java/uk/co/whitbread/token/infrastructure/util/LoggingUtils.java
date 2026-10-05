package uk.co.whitbread.token.infrastructure.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LoggingUtils {

  /**
   * Sanitizes a string for safe logging. Removes or replaces characters that could be used in log injection attacks.
   *
   * @param input the input string to sanitize
   * @return sanitized string safe for logging, or "null" if input is null
   */
  public static String sanitizeLogging(String input) {
    if (input == null) {
      return "null";
    }
    // Remove or replace potentially dangerous characters
    return input.replaceAll("[\r\n\t<>\"'&]", "_");
  }
}
