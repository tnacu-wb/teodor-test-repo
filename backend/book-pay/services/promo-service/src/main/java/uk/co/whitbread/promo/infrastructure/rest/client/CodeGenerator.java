package uk.co.whitbread.promo.infrastructure.rest.client;

import java.security.SecureRandom;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CodeGenerator {

  private static final char[] ALPHABET = "ABCDEFGHJKMNPQRSTVWXYZ23456789".toCharArray();
  private static final SecureRandom random = new SecureRandom();

  public static String generateCode(String prefix,
      int len) {
    StringBuilder sb = new StringBuilder();
    if (prefix != null && !prefix.isBlank()) {
      sb.append(prefix.toUpperCase());
    }
    for (int i = 0; i < len; i++) {
      sb.append(ALPHABET[random.nextInt(ALPHABET.length)]);
    }
    return sb.toString();
  }

}
