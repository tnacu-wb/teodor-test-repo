package uk.co.whitbread.payapp.infrastructure.util;

import static uk.co.whitbread.payapp.ErrorCode.USER_EMAIL_MISMATCH_ERROR;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.EmailMismatchException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;

@Slf4j
@Component
public class Utils {

  private final WorldlineProperties worldlineProperties;

  @Autowired
  public Utils(WorldlineProperties worldlineProperties) {
    this.worldlineProperties = worldlineProperties;
  }

  private static final Pattern IPV4_PATTERN = Pattern.compile(
      "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");

  public String getClientIp(HttpServletRequest request) {
    try {
      String ipAddress = extractClientIp(request);

      if (!isValidIpv4(ipAddress)) {
        log.warn("Invalid IP address format: {}", ipAddress);
        ipAddress = worldlineProperties.getDefaultIpAddress();
      }

      logMaskedIp(ipAddress);
      return ipAddress;

    } catch (Exception e) {
      log.warn("Could not extract client IP address from request: {}", e.getMessage());
      return worldlineProperties.getDefaultIpAddress();
    }
  }

  private String extractClientIp(HttpServletRequest request) {

    var trueClientIp = request.getHeader("true-client-ip");
    if (Strings.isNotBlank(trueClientIp) && !"unknown".equalsIgnoreCase(trueClientIp)
        && isValidIpv4(trueClientIp)) {
      return trueClientIp;
    }

    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (Strings.isNotBlank(xForwardedFor) && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      var xForwardedForIp = xForwardedFor.split(",")[0].trim();
      if (isValidIpv4(xForwardedForIp)) {
        return xForwardedForIp;
      }
    }

    log.info(
        "No valid ip found in X-Forwarded-For or true-client-ip headers, falling back to remote address.");
    return request.getRemoteAddr();
  }

  private void logMaskedIp(String ip) {
    int dotIndex = ip.indexOf('.');
    String masked = (dotIndex > 0) ? ip.substring(0, dotIndex) + ".XXX.XXX.XXX" : ip;
    log.info("Client IP address: {}", masked);
  }

  public static boolean isValidIpv4(String ipAddress) {
    return IPV4_PATTERN.matcher(ipAddress).matches();
  }

  public static void validateEmailWithToken(String email, String emailFromToken) {
    if (!email.equals(emailFromToken)) {
      throw new EmailMismatchException(USER_EMAIL_MISMATCH_ERROR,
          "Email from request body was different from email in authorization token");
    }
  }

  public static String sanitizeInputString(String input) {
    return input.replace("\n", "").replace("\r", "");
  }

}
