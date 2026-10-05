package uk.co.whitbread.reservation.infrastructure.rest.client.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.properties.HotelAvailabilityClientProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.properties.HotelAccountProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.promotion.service.properties.PromoServiceProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties.RulesAdapterProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final OhipAdapterProperties ohipAdapterProperties;
  private final BasketProperties basketProperties;
  private final RulesAdapterProperties rulesAdapterProperties;
  private final ContentProperties contentProperties;
  private final HotelAvailabilityClientProperties hotelAvailabilityClientProperties;
  private final CdhAdapterProperties cdhAdapterProperties;
  private final HotelAccountProperties hotelAccountProperties;
  private final PromoServiceProperties promoServiceProperties;

  @Bean(name = "ohipAdapterWebClient")
  public WebClient ohipAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(ohipAdapterProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "ohipAdapterTimeoutConfiguredWebClient")
  public WebClient ohipAdapterTimeoutConfiguredWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(ohipAdapterProperties.getHost())
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                ohipAdapterProperties.getConnectionTimeoutMillis())
            .responseTimeout(Duration.ofMillis(ohipAdapterProperties.getResponseTimeoutMillis()))))
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "basketWebClient")
  public WebClient basketWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(basketProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "rulesAdapterWebClient")
  public WebClient rulesAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(rulesAdapterProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "contentWebClient")
  public WebClient contentWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(contentProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "cdhAdapterWebClient")
  public WebClient cdhAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(cdhAdapterProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "hotelAvailabilityWebClient")
  public WebClient hotelAvailabilityWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelAvailabilityClientProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "hotelAccountWebClient")
  public WebClient hotelAccountWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelAccountProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "promotionWebClient")
  public WebClient promotionWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(promoServiceProperties.getHost())
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
    log.debug("{} {}{} {} Headers {}",
        clientRequest.method().name(),
        clientRequest.url().getHost(),
        clientRequest.url().getPath(),
        clientRequest.url().getQuery(),
        clientRequest.headers());
  }

  private void configureCodecs(org.springframework.http.codec.ClientCodecConfigurer configurer) {
    configurer.registerDefaults(false);
    configurer.customCodecs()
        .register(new JacksonJsonEncoder());
    configurer.customCodecs()
        .register(new JacksonJsonDecoder());
  }
}