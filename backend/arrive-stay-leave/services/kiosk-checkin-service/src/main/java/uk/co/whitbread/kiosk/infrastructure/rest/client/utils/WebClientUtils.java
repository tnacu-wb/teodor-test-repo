package uk.co.whitbread.kiosk.infrastructure.rest.client.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientResponse;

@UtilityClass
public class WebClientUtils {

  public static void logErrorResponse(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
  }

}
