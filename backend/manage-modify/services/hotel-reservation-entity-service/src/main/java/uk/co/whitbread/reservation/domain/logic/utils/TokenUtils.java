package uk.co.whitbread.reservation.domain.logic.utils;

import java.security.GeneralSecurityException;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenUtils {

  public static String getToken(final String basketRef) {

    var text = basketRef + "|" + Instant.now().getEpochSecond();
    try {
      return CipherUtils.encryptWithPrefixInitVector(text);
    } catch (GeneralSecurityException e) {
      log.error("Error while generating token", e);
      return null;
    }
  }

  public static String parseToken(String token) {
    return token.replace(" ", "+");
  }

  public static Boolean isValid(final String token, final String basketRef) {

    try {
      var initialStringParts = CipherUtils.decryptWithPrefixInitVector(parseToken(token)).split("\\|");
      if (!basketRef.equals(initialStringParts[0])) {
        return Boolean.FALSE;
      }
      var timestamp = Long.valueOf(initialStringParts[1]);
      if (Instant.now().getEpochSecond() - timestamp > 1800) {
        return Boolean.FALSE;
      }
      return Boolean.TRUE;
    } catch (Exception e) {
      log.error("Error while validating the token", e);
      return Boolean.FALSE;
    }
  }
}
