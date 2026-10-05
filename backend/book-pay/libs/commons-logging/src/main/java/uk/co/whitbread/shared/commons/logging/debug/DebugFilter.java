package uk.co.whitbread.shared.commons.logging.debug;

import static java.util.Objects.nonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.owasp.encoder.Encode;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;
import uk.co.whitbread.shared.commons.logging.config.ContextConfig;

@Slf4j
@RequiredArgsConstructor
public class DebugFilter extends OncePerRequestFilter {

  private static final String QUERY_DELIMITER = "?";
  private static final Set<String> DEFAULT_EXCLUSIONS = Set.of(".*/actuator/.*");
  private final LoggingConfigurationProperties properties;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response, @NonNull FilterChain chain)
        throws ServletException, IOException {
      var shouldLog = shouldLogSession(request);
      ContextConfig.setShouldLog(shouldLog, request);
      try {
        if (shouldLog) {
          log.info("Request: method = {}, uri = {}", sanitize(request.getMethod()),
              sanitize(getUri(request)));
        }
        chain.doFilter(request, response);
      } finally {
        if (shouldLog) {
          log.info("Response: status = {}", response.getStatus());
        }
      }
    }

  private boolean shouldLogSession(HttpServletRequest request) {
    var debugProperties = Optional.ofNullable(properties)
        .map(LoggingConfigurationProperties::getDebug);
    if (debugProperties.isPresent()) {
      var debug = debugProperties.get();
      boolean isExcludedLog = Optional.of(debug)
          .map(props -> Optional.ofNullable(props.getExclusions())
              .orElse(DEFAULT_EXCLUSIONS))
          .map(exclusionURIs -> exclusionURIs.stream().noneMatch(request.getRequestURI()::matches))
          .orElse(true);
      return isExcludedLog && debug.isEnabled();
    } else {
      return false;
    }
  }

  private String sanitize(String input) {
    return Encode.forHtmlContent(input);
  }
    private String getUri(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (nonNull(queryString)) {
            return request.getRequestURI() + QUERY_DELIMITER + queryString;
        }
        return request.getRequestURI();
    }
}
