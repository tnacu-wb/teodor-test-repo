package uk.co.whitbread.shared.commons.logging.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServletServerHttpRequest;

public class ContextConfig {
  public static final String SHOULD_LOG_KEY = "SHOULD_LOG";

  private ContextConfig() {
  }

  public static boolean shouldLog(ServletServerHttpRequest serverHttpRequest) {
    if (serverHttpRequest == null) {
      return false;
    }
    return (Boolean) serverHttpRequest.getAttributes().getOrDefault(SHOULD_LOG_KEY, false);
  }

  public static void setShouldLog(boolean shouldLog, HttpServletRequest request) {
    request.setAttribute(SHOULD_LOG_KEY, shouldLog);
  }
}
