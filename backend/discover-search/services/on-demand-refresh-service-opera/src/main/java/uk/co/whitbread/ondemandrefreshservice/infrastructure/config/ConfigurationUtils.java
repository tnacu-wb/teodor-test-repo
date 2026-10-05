package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
public class ConfigurationUtils {

  private ConfigurationUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private static void logRequest(ClientRequest clientRequest) {
    log.info("WebClient Request: method = {}, uri = {}{} query = {} headers = {}",
        clientRequest.method().name(),
        clientRequest.url().getHost(),
        clientRequest.url().getPath(),
        clientRequest.url().getQuery(),
        clientRequest.headers());
  }
}