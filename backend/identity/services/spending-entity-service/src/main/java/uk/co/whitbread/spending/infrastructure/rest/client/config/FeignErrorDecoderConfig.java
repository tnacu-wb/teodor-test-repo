package uk.co.whitbread.spending.infrastructure.rest.client.config;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

public class FeignErrorDecoderConfig {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new LoggingErrorDecoder();
  }

  @Slf4j
  static class LoggingErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
      try {
        String responseBody = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
        log.error("Error occurred while calling {}: status code {}, reason {}, response: {}.",
            methodKey, response.status(), response.reason(), responseBody);
      } catch (Exception e) {
        log.error("Error occurred while calling {}: status code {}, reason {}",
            methodKey, response.status(), response.reason());
      }

      var result = defaultErrorDecoder.decode(methodKey, response);
      ExceptionLogger.log(log, result);

      return result;
    }
  }
}
