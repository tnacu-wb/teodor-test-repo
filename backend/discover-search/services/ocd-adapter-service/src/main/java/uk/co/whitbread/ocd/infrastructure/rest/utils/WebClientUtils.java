package uk.co.whitbread.ocd.infrastructure.rest.utils;

import java.util.Map;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.ClientResponse;

@UtilityClass
public class WebClientUtils {

  public static void logErrorResponse(Logger log, ClientResponse response) {
    logErrorHeader(log, response);
    Map<String, String> contextMdc = MDC.getCopyOfContextMap();
    response.bodyToMono(Object.class)
        .subscribe(body -> {
          if (contextMdc != null) {
            MDC.setContextMap(contextMdc);
          }
          log.error("Response body: {}", body);
        });
  }

  public static void logErrorHeader(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
  }

}
