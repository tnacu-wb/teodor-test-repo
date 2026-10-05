package uk.co.whitbread.shared.commons.logging.debug;

import java.lang.reflect.Type;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties.DebugProperties;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties.HTTPMessage;
import uk.co.whitbread.shared.commons.logging.config.ContextConfig;

@ControllerAdvice
@ConditionalOnProperty(value = "logging.configuration.debug.enabled", havingValue = "true")
@Slf4j
@RequiredArgsConstructor
public class DebugRequestBodyAdvice extends RequestBodyAdviceAdapter {

  private final LoggingConfigurationProperties properties;

  /**
   * Logs the request body after it was converted to an Object. Method called only when the request has a body.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public @NonNull Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
      @NonNull MethodParameter parameter, @NonNull Type targetType,
      @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
    if (logBody(inputMessage)) {
      log.info("Request: body = {}", body);
    }
    return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type type,
      @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
    return true;
  }

  private boolean logBody(HttpInputMessage inputMessage) {
    ServletServerHttpRequest servletServerHttpRequest =
        inputMessage instanceof ServletServerHttpRequest request ? request : null;
    return ContextConfig.shouldLog(servletServerHttpRequest) && Optional.ofNullable(properties)
        .map(LoggingConfigurationProperties::getDebug)
        .map(DebugProperties::getRequest)
        .map(HTTPMessage::isBody)
        .orElse(true);
  }
}