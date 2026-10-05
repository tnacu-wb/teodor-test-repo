package uk.co.whitbread.basket.infrastructure.rest.client.config;

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
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.hotel.service.properties.HotelInfoClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.properties.MarketingClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.properties.OhipAdapterProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties.RefundRequestProcessorProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties.ThreecProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.promotion.service.properties.PromoServiceProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.reservation.service.properties.ReservationClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.client.rules.service.properties.RulesAgentClientProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final ThreecProperties threecProperties;
  private final JsonMapper jsonMapper;
  private final ReservationClientProperties reservationProperties;
  private final ContentProperties contentProperties;
  private final HotelInfoClientProperties hotelInfoClientProperties;
  private final MarketingClientProperties marketingClientProperties;
  private final RulesAgentClientProperties rulesAgentClientProperties;
  private final RefundRequestProcessorProperties refundRequestProcessorProperties;
  private final OhipAdapterProperties ohipAdapterProperties;
  private final CdhAdapterProperties cdhAdapterProperties;
  private final PromoServiceProperties promoServiceProperties;

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

  @Bean(name = "paymentsWebClient")
  public WebClient paymentsWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(threecProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "rrpWebClient")
  public WebClient rrpWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
            .baseUrl(refundRequestProcessorProperties.getHost())
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

  @Bean(name = "hotelInfoWebClient")
  public WebClient hotelInfoWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(hotelInfoClientProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "marketingWebClient")
  public WebClient marketingWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(marketingClientProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "rulesAgentWebClient")
  public WebClient rulesAgentWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(rulesAgentClientProperties.getHost())
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

  @Bean(name = "promoWebClient")
  public WebClient promoWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(promoServiceProperties.getHost())
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