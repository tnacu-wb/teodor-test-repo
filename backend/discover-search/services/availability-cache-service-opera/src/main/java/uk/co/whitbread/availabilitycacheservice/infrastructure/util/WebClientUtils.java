package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientResponse;

@UtilityClass
public class WebClientUtils {

  public static void logErrorResponse(Logger log, ClientResponse response) {
    String mess = String.format("Response status: %s. Response headers: %s ",
        response.statusCode(),
        response.headers().asHttpHeaders());
    log.error(mess);
  }

  public static void logErrorHeader(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
  }

}
