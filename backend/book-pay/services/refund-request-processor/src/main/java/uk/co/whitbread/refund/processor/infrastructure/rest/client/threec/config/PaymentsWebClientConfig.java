package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.properties.ThreecProperties;

@Data
@Slf4j
@Configuration
public class PaymentsWebClientConfig {

  private final ThreecProperties threecProperties;
  private final ObjectMapper objectMapper;

  @Bean(name = "paymentsWebClient")
  public WebClient paymentsWebClient(WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(threecProperties.getHost())
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
                      .register(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
              configurer.customCodecs()
                      .register(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            }).build();
  }
}
