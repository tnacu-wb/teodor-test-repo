package uk.co.whitbread.payments.infrastructure.rest.client.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.service.properties.BasketClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties.CompanyServiceClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties.HotelAccountClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties.HotelCardClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.properties.HotelInfoClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.service.properties.ReservationClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.token.service.properties.TokenClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.hotelentity.properties.HotelEntityClientProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final HotelCardClientProperties hotelCardClientProperties;
  private final ReservationClientProperties reservationProperties;
  private final HotelInfoClientProperties hotelInfoClientProperties;
  private final TokenClientProperties tokenProperties;
  private final HotelAccountClientProperties hotelAccountClientProperties;
  private final CompanyServiceClientProperties companyServiceClientProperties;
  private final BasketClientProperties basketClientProperties;
  private final HotelEntityClientProperties hotelEntityClientProperties;
  private final JsonMapper jacksonJsonMapper;

  @Bean(name = "hotelCardWebClient")
  public WebClient hotelCardWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelCardClientProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "reservationWebClient")
  public WebClient reservationWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(reservationProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "tokenWebClient")
  public WebClient tokenWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(tokenProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "hotelInfoWebClient")
  public WebClient contentWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelInfoClientProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "hotelAccountWebClient")
  public WebClient hotelAccountWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelAccountClientProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public WebClient companyServiceWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(companyServiceClientProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "basketWebClient")
  public WebClient basketWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(basketClientProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "hotelEntityWebClient")
  public WebClient hotelEntityWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelEntityClientProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
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

  private void configureCodecs(CodecConfigurer configurer, JsonMapper jsonMapper) {
    configurer.registerDefaults(false);
    configurer.customCodecs()
        .register(new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
    configurer.customCodecs()
        .register(new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
  }

}
