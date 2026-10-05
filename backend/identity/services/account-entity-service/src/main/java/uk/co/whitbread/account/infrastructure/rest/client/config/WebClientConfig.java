package uk.co.whitbread.account.infrastructure.rest.client.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.account.infrastructure.rest.client.customers.service.properties.CustomerRegistrationClientProperties;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.properties.MarketingPreferencesClientProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {
  private final CustomerRegistrationClientProperties customerRegistrationClientProperties;
  private final MarketingPreferencesClientProperties marketingPreferenceClientProperties;
  private final JsonMapper objectMapper;

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("connectionProvider")
        .maxIdleTime(Duration.ofSeconds(10))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofSeconds(120))
        .maxConnections(500)
        .build();
  }

  @Bean
  public HttpClient httpClient(@Value("${config.httpClient.connectionTimeout:10000}") Integer connectionTimeout,
      @Value("${config.httpClient.responseTimeout:30}") Long responseTimeout) {
    return HttpClient.create(connectionProvider())
        //connection timeout is a period within which a connection between a client and a server must be established
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
        //TCP check probes when the connection is idle
        .option(ChannelOption.SO_KEEPALIVE, true)
        //response timeout is the time we wait to receive a response after sending a request
        .responseTimeout(Duration.ofSeconds(responseTimeout))
        //Keep-Alive support for the outgoing request
        .keepAlive(true);
  }

  @Bean(name = "customerRegistrationWebClient")
  public WebClient customerRegistrationWebClient(HttpClient httpClient) {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(customerRegistrationClientProperties.getHost())
        .exchangeStrategies(exchangeStrategies())
        .filter(logRequest())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "marketingPreferencesWebClient")
  public WebClient marketingPreferencesWebClient(HttpClient httpClient) {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(marketingPreferenceClientProperties.getHost())
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
              .register(new JacksonJsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
          configurer.customCodecs()
              .register(new JacksonJsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
        }).build();
  }
}
