package uk.co.whitbread.cdh.infrastructure.rest.client.config;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.PrematureCloseException;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.WebClientProperties;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;
  private final WebClientProperties webClientProperties;
  private final JsonMapper jsonMapper;
  private static final int MAX_RETRIES = 3;
  private static final int MIN_BACKOFF = 1;

  @Bean
  @ConditionalOnMissingBean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }

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

  @Bean(name = "cdhAccountServicesWebclient")
  public WebClient cdhAccountServicesWebclient(HttpClient httpClient) {
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(configurer -> {
          configurer.defaultCodecs().maxInMemorySize(webClientProperties.getSize() * 1024 * 1024);
          configurer.defaultCodecs().jacksonJsonEncoder(
              new JacksonJsonEncoder(jsonMapper, MediaType.APPLICATION_JSON));
          configurer.defaultCodecs().jacksonJsonDecoder(
              new JacksonJsonDecoder(jsonMapper, MediaType.APPLICATION_JSON));
        })
        .build();
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(cdhApiProperties.getHost())
        .filter(logRequest())
        .filter(logResponse())
        .filter(((request, next) ->
            next.exchange(request)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                    .filter(ex -> ex instanceof PrematureCloseException
                        || (ex instanceof WebClientRequestException
                        &&  ex.getCause() instanceof PrematureCloseException)))
        ))
        .exchangeStrategies(strategies)
        .defaultHeaders(cdhAccountServicesDefaultHeaders())
        .build();
  }

  private Consumer<HttpHeaders> cdhAccountServicesDefaultHeaders() {
    return headers -> {
      headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      headers.set(cdhApiOauthProperties.getSubscriptionKeyHeaderName(),
          cdhApiOauthProperties.getAccountSubscriptionKey());
      headers.set(cdhApiProperties.getRequestHeaderName(), cdhApiProperties.getRequestHeaderValue());
    };
  }

  private ExchangeFilterFunction logRequest() {

    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private void logRequest(ClientRequest clientRequest) {

    log.info("{} {} {} {} \n--Headers {}", clientRequest.method().name(),
        clientRequest.url().getHost(), clientRequest.url().getPath(),
        clientRequest.url().getQuery(), clientRequest.headers());
  }

  private ExchangeFilterFunction logResponse() {

    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("CDH API Response: {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }

}
