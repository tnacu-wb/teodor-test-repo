package uk.co.whitbread.infrastructure.config.snowdrop;

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
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.infrastructure.rest.client.SnowDropClient;

@Data
@Slf4j
@Configuration
public class ClientSnowdropBeanConfig {

  private final SnowDropProperties snowDropProperties;
  private final JsonMapper objectMapper;

  @Bean(name = "snowDropWebClient")
  public WebClient snowDropWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(
            HttpClient.create().followRedirect(true)))
        .baseUrl(snowDropProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean
  public SnowDropClient snowDropClient(final WebClient snowDropWebClient,
      final SnowDropProperties snowDropProperties) {
    return new SnowDropClient(snowDropWebClient, snowDropProperties);
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
    JsonMapper decoderMapper = objectMapper.rebuild()
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
    return ExchangeStrategies.builder().codecs(
        configurer -> {
          configurer.registerDefaults(false);
          configurer.customCodecs()
              .register(new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
          configurer.customCodecs()
              .register(new JacksonJsonDecoder(decoderMapper, MediaType.APPLICATION_JSON));
        }).build();
  }
}