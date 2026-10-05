package uk.co.whitbread.shared.commons.logging.debug;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties.DebugProperties;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties.HTTPMessage;
import uk.co.whitbread.shared.commons.logging.config.ContextConfig;

@ControllerAdvice
@ConditionalOnProperty(value = "logging.configuration.debug.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class DebugResponseBodyAdvice implements
    org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice<Object> {

  private final LoggingConfigurationProperties properties;

  /**
   * Logs the response body. Method called only when the response has a body.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Object beforeBodyWrite(@Nullable Object body,
      @NonNull MethodParameter methodParameter,
      @NonNull MediaType mediaType,
      @NonNull Class<? extends HttpMessageConverter<?>> aClass,
      @NonNull ServerHttpRequest serverHttpRequest,
      @NonNull ServerHttpResponse serverHttpResponse) {
    if (logBody(serverHttpRequest)) {
      log.info("Response: body = {}", body);
    }
    return body;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supports(@NonNull MethodParameter methodParameter,
      @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  private boolean logBody(ServerHttpRequest serverHttpRequest) {
    ServletServerHttpRequest servletServerHttpRequest =
        serverHttpRequest instanceof ServletServerHttpRequest request ? request : null;
    return ContextConfig.shouldLog(servletServerHttpRequest) && Optional.ofNullable(properties)
        .map(LoggingConfigurationProperties::getDebug)
        .map(DebugProperties::getResponse)
        .map(HTTPMessage::isBody)
        .orElse(true);
  }
}