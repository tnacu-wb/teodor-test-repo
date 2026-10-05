package uk.co.whitbread.reservation.infrastructure.rest.utils;

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

}