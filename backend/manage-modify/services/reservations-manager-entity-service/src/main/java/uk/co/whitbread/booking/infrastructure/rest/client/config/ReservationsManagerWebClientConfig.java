package uk.co.whitbread.booking.infrastructure.rest.client.config;

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
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties.BookingProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties.HotelAccountClientProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.content.properties.ContentProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties.ReservationProperties;

@Data
@Slf4j
@Configuration
public class ReservationsManagerWebClientConfig {

  private final BookingProperties bookingProperties;
  private final HotelAccountClientProperties hotelAccountClientProperties;
  private final ReservationProperties reservationProperties;
  private final ContentProperties contentProperties;
  private final BasketProperties basketProperties;
  private final JsonMapper jsonMapper;
  private final OhipAdapterProperties ohipAdapterProperties;
  private final CdhAdapterProperties cdhAdapterProperties;

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean(name = "hotelAccountWebClient")
  public WebClient hotelAccountWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
            .baseUrl(hotelAccountClientProperties.getHost())
            .exchangeStrategies(exchangeStrategies())
            .filter(logRequest())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  @Bean(name = "bookingWebClient")
  public WebClient bookingWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(bookingProperties.getUrl())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "reservationWebClient")
  public WebClient reservationWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(reservationProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "contentWebClient")
  public WebClient contentWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(contentProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "basketWebClient")
  public WebClient basketWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
            .baseUrl(basketProperties.getHost())
            .exchangeStrategies(exchangeStrategies())
            .filter(logRequest())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  @Bean(name = "ohipAdapterWebClient")
  public WebClient ohipAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(ohipAdapterProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "cdhAdapterWebClient")
  public WebClient cdhAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(cdhAdapterProperties.getHost())
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
    log.debug("{} {}{} {} Headers {}",
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
}
