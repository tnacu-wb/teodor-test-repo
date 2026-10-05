package uk.co.whitbread.avail.business.events.infrastructure.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class HashingUtils {

  private HashingUtils() {}

  /**
   * Returns a hexadecimal encoded SHA-256 hash for the input String.
   */
  public static String getSha256Hash(final String data) {
    String lowerCaseHash = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
      lowerCaseHash = HexFormat.of().formatHex(hash);
    } catch (Exception ex) {
      log.error("Exception while hashing: {}", ex.getMessage());
    }
    return lowerCaseHash;
  }

  public static String maskStringExceptLast4(String input) {
    if (StringUtils.isNotBlank(input) && input.length() >= 4) {
      return "*".repeat(input.length() - 4) + input.substring(input.length() - 4);
    } else {
      return input;
    }
  }

}
