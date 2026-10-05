package uk.co.whitbread.shared.cdh.config;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider.Proxy;
import uk.co.whitbread.shared.cdh.model.CdhAccessContext;
import uk.co.whitbread.shared.cdh.model.CdhHeaders;
import uk.co.whitbread.shared.cdh.oauth.OAuthProvider;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebClientConfig {

  private final OAuthProvider oauthProvider;

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("cdhConnectionProvider")
        .maxIdleTime(Duration.ofSeconds(10))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofSeconds(120))
        .maxConnections(500)
        .build();
  }

  @Bean("cdhHttpClient")
  public HttpClient cdhHttpClient(@Value("${config.httpClient.connectionTimeout:10000}") Integer connectionTimeout,
      @Value("${config.httpClient.responseTimeout:30}") Long responseTimeout,
      @Value("${config.httpClient.proxy.host:}") String proxyHost,
      @Value("${config.httpClient.proxy.port:0}") Integer proxyPort) {
    HttpClient httpClient = HttpClient.create(connectionProvider())
        //connection timeout is a period within which a connection between a client and a server must be established
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
        //TCP check probes when the connection is idle
        .option(ChannelOption.SO_KEEPALIVE, true)
        //response timeout is the time we wait to receive a response after sending a request
        .responseTimeout(Duration.ofSeconds(responseTimeout))
        //Keep-Alive support for the outgoing request
        .keepAlive(true);
    if (isNotEmpty(proxyHost) && proxyPort > 0) {
      httpClient = httpClient.proxy(proxy -> proxy
          .type(Proxy.HTTP)
          .host(proxyHost)
          .port(proxyPort)
      );
    }
    return httpClient;
  }

  @Bean("cdhWebClient")
  public WebClient cdhWebClient(@Qualifier("cdhHttpClient") HttpClient httpClient) {
    final int size = 16 * 1024 * 1024;

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .filter(logRequest())
        .filter(logResponse())
        .filter(retryOn401Function())
        .exchangeStrategies(ExchangeStrategies.builder().codecs(
            configurer -> {
              configurer.defaultCodecs().maxInMemorySize(size);
            }).build())
        .defaultHeaders(httpHeaders -> httpHeaders.addAll(createDefaultHeaders()))
        .build();
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

  private ExchangeFilterFunction retryOn401Function() {
    return (request, next) -> next.exchange(request)
        .flatMap((Function<ClientResponse, Mono<ClientResponse>>) clientResponse -> {
          if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
            log.debug("Got 401 UNAUTHORIZED Response - Refreshing Auth Token");
            String newToken = oauthProvider.getNewBearerToken();
            ClientRequest retryRequest = ClientRequest.from(request)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(newToken)).build();
            logRequest(retryRequest);
            return next.exchange(retryRequest);
          } else {
            return Mono.just(clientResponse);
          }
        });
  }

  private HttpHeaders createDefaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    headers.add(CdhHeaders.ACCESS_CONTEXT.getHeader(), CdhAccessContext.PI.name());
    return headers;
  }

}
