package uk.co.whitbread.infrastructure.rest.client.utils;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@UtilityClass
public class WebClientUtils {

  public static Mono<Void> logErrorResponse(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
    return response.bodyToMono(String.class)
        .doOnNext(body -> log.error("Response body: {}", body))
        .onErrorResume(err -> {
          log.warn("Could not read response body: {}", err.getMessage());
          return Mono.empty();
        })
        .then();
  }

  public static void logErrorHeader(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
  }

}
