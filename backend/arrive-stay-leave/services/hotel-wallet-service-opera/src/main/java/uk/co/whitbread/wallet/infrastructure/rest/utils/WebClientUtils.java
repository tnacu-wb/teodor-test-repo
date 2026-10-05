package uk.co.whitbread.wallet.infrastructure.rest.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientResponse;

@UtilityClass
public class WebClientUtils {

  public static void logErrorResponse(Logger log, ClientResponse response) {
    logErrorStatusAndHeaders(log, response);
    response.bodyToMono(Object.class)
        .contextCapture()
        .subscribe(body -> log.error("Response body: {}", body));
  }

  public static void logErrorStatusAndHeaders(Logger log, ClientResponse response) {
    log.error("Response status: {}. Response headers: {} ", response.statusCode(),
        response.headers().asHttpHeaders());
  }

}