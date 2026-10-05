package uk.co.whitbread.digitalkey.infrastructure.rest.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.properties.KioskClientProperties;
import uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final AxpProperties axpProperties;
  private final OhipAdapterProperties ohipAdapterProperties;
  private final JsonMapper jsonMapper;
  private final KioskClientProperties kioskClientProperties;

  @Bean(name = "axpWebClient")
  public WebClient axpWebClient() {
    return WebClient.builder()
        .baseUrl(axpProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .build();
  }

  @Bean("ohipAdapterWebClient")
  public WebClient ohipAdapterWebClient() {
    return WebClient.builder()
        .baseUrl(ohipAdapterProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private void logRequest(ClientRequest clientRequest) {
    log.info("{} {}{} {} Headers {}",
        clientRequest.method().name(),
        clientRequest.url().getHost(),
        clientRequest.url().getPath(),
        clientRequest.url().getQuery(),
        clientRequest.headers());
  }

  private ExchangeStrategies exchangeStrategies() {
    return ExchangeStrategies.builder().codecs(
        configurer -> {
          configurer.registerDefaults(false);
          configurer.customCodecs()
              .register(new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
          configurer.customCodecs()
              .register(new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
        }).build();
  }

  @Bean("kioskWebClient")
  public WebClient kioskWebClient() {
    return WebClient.builder()
        .baseUrl(kioskClientProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

}