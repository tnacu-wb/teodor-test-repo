package uk.co.whitbread.hotel.card.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.properties.WorldlineRestProperties;

@Slf4j
@Component
public class NetworkUtils {

  private final WorldlineRestProperties worldlineRestProperties;

  @Autowired
  public NetworkUtils(WorldlineRestProperties worldlineRestProperties) {
    this.worldlineRestProperties = worldlineRestProperties;
  }

  private static final Pattern IPV4_PATTERN = Pattern.compile(
      "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");

  public String getClientIp(HttpServletRequest request) {
    try {
      String ipAddress = request.getHeader("X-Forwarded-For");
      if (Strings.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
        ipAddress = request.getRemoteAddr();
      } else {
        // In case of multiple IP addresses, the first one is the client's IP
        ipAddress = ipAddress.split(",")[0];
      }

      if (Strings.isBlank(ipAddress) || !isValidIpv4(ipAddress)) {
        ipAddress = worldlineRestProperties.getDefaultIpAddress();
      }

      return ipAddress;
    } catch (Exception e) {
      log.warn("Could not extract client IP address from request: {}", e.getMessage());
      return worldlineRestProperties.getDefaultIpAddress();
    }
  }

  public static boolean isValidIpv4(String ipAddress) {
    return IPV4_PATTERN.matcher(ipAddress).matches();
  }
}