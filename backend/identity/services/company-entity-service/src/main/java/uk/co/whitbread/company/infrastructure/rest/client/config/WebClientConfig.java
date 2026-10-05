package uk.co.whitbread.company.infrastructure.rest.client.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
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
import uk.co.whitbread.company.infrastructure.rest.client.cdh.config.CdhProperties;
import uk.co.whitbread.company.infrastructure.rest.client.ohip.config.OhipProperties;

@RequiredArgsConstructor
@Slf4j
@Data
@Configuration
public class WebClientConfig {

  private final CdhProperties cdhProperties;
  private final OhipProperties ohipProperties;
  private final JsonMapper jsonMapper;

  @Bean(name = "cdhServiceWebClient")
  public WebClient cdhServiceWebClient() {
    return WebClient.builder()
        .baseUrl(cdhProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "ohipServiceWebClient")
  public WebClient ohipServiceWebClient() {
    final int size = 16 * 1024 * 1024;
    return WebClient.builder()
        .baseUrl(ohipProperties.getHost())
        .exchangeStrategies(ExchangeStrategies.builder().codecs(
            configurer -> {
              configurer.defaultCodecs().maxInMemorySize(size);
              configurer.defaultCodecs().jacksonJsonEncoder(
                  new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
              configurer.defaultCodecs().jacksonJsonDecoder(
                  new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
            }).build())
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
          configurer.defaultCodecs().jacksonJsonEncoder(
              new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
          configurer.defaultCodecs().jacksonJsonDecoder(
              new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
        }).build();
  }
}
