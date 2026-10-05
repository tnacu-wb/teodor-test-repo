package uk.co.whitbread.wallet.infrastructure.rest.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.wallet.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.properties.HotelReservationsProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {
  
  private final JsonMapper jsonMapper;
  private final HotelReservationsProperties reservationProperties;
  private final ContentProperties contentProperties;
  
  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean(name = "reservationWebClient")
  public WebClient reservationWebClient() {
    return WebClient.builder()
        .baseUrl(reservationProperties.getReservationHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
  
  @Bean(name = "contentWebClient")
  public WebClient contentWebClient() {
    return WebClient.builder()
        .baseUrl(contentProperties.getHost())
        .codecs(this::configureCodecs)
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
  
  private void configureCodecs(ClientCodecConfigurer configurer) {
    configurer.defaultCodecs()
        .maxInMemorySize(16 * 1024 * 1024);
    configurer.defaultCodecs().jacksonJsonEncoder(
        new JacksonJsonEncoder(
            jsonMapper, MediaType.APPLICATION_JSON));
    configurer.defaultCodecs().jacksonJsonDecoder(
        new JacksonJsonDecoder(
            jsonMapper, MediaType.APPLICATION_JSON));
  }
}
