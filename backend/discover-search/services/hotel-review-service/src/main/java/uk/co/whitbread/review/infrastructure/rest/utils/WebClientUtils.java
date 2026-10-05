package uk.co.whitbread.review.infrastructure.rest.utils;

import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class WebClientUtils {
  public static void logErrorResponse(Logger log, ClientResponse response) {
    log.error("Response status: {}", response.statusCode());
    log.error("Response headers: {}", response.headers().asHttpHeaders());
    response.bodyToMono(Object.class)
            .contextCapture()
            .subscribe(body -> log.error("Response body: {}", body));
  }

  public static ExchangeFilterFunction logRequest(Logger log) {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest, log);
      return Mono.just(clientRequest);
    });
  }

  public static void logRequest(ClientRequest clientRequest, Logger log) {
    log.info("{} {}{} {} Headers {}",
            clientRequest.method().name(),
            clientRequest.url().getHost(),
            clientRequest.url().getPath(),
            clientRequest.url().getQuery(),
            clientRequest.headers());
  }
}
