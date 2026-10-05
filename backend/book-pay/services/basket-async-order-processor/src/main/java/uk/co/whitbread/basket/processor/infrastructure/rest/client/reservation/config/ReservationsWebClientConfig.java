package uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.properties.ReservationsClientProperties;

@Data
@Slf4j
@Configuration
public class ReservationsWebClientConfig {

  private static final int MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024; // 16 MB

  private final ReservationsClientProperties reservationsClientProperties;
  private final ObjectMapper objectMapper;

  public ReservationsWebClientConfig(
      ReservationsClientProperties reservationsClientProperties,
      ObjectMapper objectMapper) {
    this.reservationsClientProperties = reservationsClientProperties;
    this.objectMapper = objectMapper;
  }

  @Bean(name = "reservationsWebClient")
  public WebClient reservationsWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(reservationsClientProperties.getHost())
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
          configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE);
        }).build();
  }
}