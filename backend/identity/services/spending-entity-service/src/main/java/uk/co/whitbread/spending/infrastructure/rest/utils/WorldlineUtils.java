package uk.co.whitbread.spending.infrastructure.rest.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import uk.co.whitbread.spending.domain.model.in.worldline.TrustedPartnerCredentials;

@Slf4j
@UtilityClass
public class WorldlineUtils {

  public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
  public static final String TRUE_CLIENT_IP_HEADER = "true-client-ip";
  private static final String UNKNOWN = "unknown";
  private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static String serializeHeader(TrustedPartnerCredentials authHeader) {
    try {
      return OBJECT_MAPPER.writeValueAsString(authHeader);
    } catch (JsonProcessingException e) {
      log.error("Error during TrustedPartnerCredentials header serialization.", e);
      return null;
    }
  }

  public String getClientIp(HttpServletRequest request, String defaultIpAddress) {
    try {
      String ipAddress = extractClientIp(request);

      if (!isValidIpv4(ipAddress)) {
        log.warn("Invalid IP address format: {}", ipAddress);
        ipAddress = defaultIpAddress;
      }

      logMaskedIp(ipAddress);
      return ipAddress;

    } catch (Exception e) {
      log.warn("Could not extract client IP address from request: {}", e.getMessage());
      return defaultIpAddress;
    }
  }

  private String extractClientIp(HttpServletRequest request) {

    var trueClientIp = request.getHeader(TRUE_CLIENT_IP_HEADER);
    if (Strings.isNotBlank(trueClientIp) && !UNKNOWN.equalsIgnoreCase(trueClientIp)
        && isValidIpv4(trueClientIp)) {
      return trueClientIp;
    }

    String xForwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
    if (Strings.isNotBlank(xForwardedFor) && !UNKNOWN.equalsIgnoreCase(xForwardedFor)) {
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

}
