package uk.co.whitbread.avail.business.events.infrastructure.config;

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
import uk.co.whitbread.avail.business.events.infrastructure.client.content.service.properties.ContentProperties;
import uk.co.whitbread.avail.business.events.infrastructure.client.ocd.service.properties.OcdAdapterProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final ContentProperties contentProperties;
  private final OcdAdapterProperties ocdAdapterProperties;
  private final JsonMapper jsonMapper;

  @Bean(name = "contentWebClient")
  public WebClient contentWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(contentProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "ocdAdapterWebClient")
  public WebClient ocdAdapterWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(ocdAdapterProperties.getHost())
        .codecs(this::configureCodecs)
        .filter(logRequest())
        .defaultHeader(
            HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
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
}