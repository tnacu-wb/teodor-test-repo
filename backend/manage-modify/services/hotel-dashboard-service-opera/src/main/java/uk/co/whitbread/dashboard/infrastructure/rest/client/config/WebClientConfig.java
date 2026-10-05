package uk.co.whitbread.dashboard.infrastructure.rest.client.config;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.client.content.service.properties.ContentProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service.properties.HotelAccountProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo.properties.HotelInfoProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service.properties.HotelReservationProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final JsonMapper jsonMapper;
  private final HotelReservationProperties reservationProperties;
  private final HotelInfoProperties hotelInfoProperties;
  private final HotelAccountProperties hotelAccountProperties;
  private final CdhAdapterProperties cdhAdapterProperties;
  private final ContentProperties contentProperties;

  @Bean("hotelInfoWebClient")
  public WebClient hotelInfoWebClient() {
    return WebClient.builder()
        .baseUrl(hotelInfoProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("hotelAccountWebClient")
  public WebClient hotelAccountWebClient() {
    return WebClient.builder()
        .baseUrl(hotelAccountProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("cdhAdapterWebClient")
  public WebClient cdhAdapterWebClient() {
    return WebClient.builder()
        .baseUrl(cdhAdapterProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("hotelReservationWebClient")
  public WebClient hotelReservationWebClient() {
    return WebClient.builder()
        .baseUrl(reservationProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jsonMapper))
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean("contentWebClient")
  public WebClient contentWebClient() {
    return WebClient.builder()
        .baseUrl(contentProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jsonMapper))
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

  private void configureCodecs(CodecConfigurer configurer, JsonMapper jsonMapper) {
    JsonMapper configuredMapper = jsonMapper.rebuild()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .changeDefaultPropertyInclusion(
            incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL)
                .withValueInclusion(JsonInclude.Include.NON_NULL))
        .build();
    configurer.registerDefaults(false);
    configurer.customCodecs()
        .register(new JacksonJsonEncoder(configuredMapper, MediaType.APPLICATION_JSON));
    configurer.customCodecs()
        .register(new JacksonJsonDecoder(configuredMapper, MediaType.APPLICATION_JSON));
  }

}
