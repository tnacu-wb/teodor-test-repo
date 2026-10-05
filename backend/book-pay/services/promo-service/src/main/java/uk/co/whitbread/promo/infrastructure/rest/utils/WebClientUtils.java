package uk.co.whitbread.promo.infrastructure.rest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Mono;

@UtilityClass
public class WebClientUtils {

  public static void logErrorResponse(Logger log, ClientResponse response) {
    String mess = String.format("Response status: %s. Response headers: %s ",
        response.statusCode(),
        response.headers().asHttpHeaders());
    log.error(mess);
  }

  public static ExchangeStrategies exchangeStrategies(ObjectMapper objectMapper) {
    return ExchangeStrategies.builder().codecs(
        configurer -> {
          configurer.registerDefaults(false);
          configurer.customCodecs()
              .register(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
          configurer.customCodecs()
              .register(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
        }).build();
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
