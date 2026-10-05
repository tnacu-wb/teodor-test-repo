package uk.co.whitbread.refund.processor.infrastracture.rest.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientResponse;

@UtilityClass
public class WebClientUtils {

  public static void logErrorResponse(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
    response.bodyToMono(Object.class)
        .contextCapture()
                .subscribe(body -> log.error("Response body: {}", body));
  }

}