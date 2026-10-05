package uk.co.whitbread.promo.infrastructure.config;

import static uk.co.whitbread.promo.infrastructure.rest.utils.WebClientUtils.exchangeStrategies;
import static uk.co.whitbread.promo.infrastructure.rest.utils.WebClientUtils.logRequest;

import com.fasterxml.jackson.databind.json.JsonMapper;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.properties.OhipAdapterProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final OhipAdapterProperties ohipAdapterProperties;

  @Bean
  public JsonMapper objectMapper() {
    return JsonMapper.builder()
            .findAndAddModules()
            .build();
  }

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }


  @Bean("ohipAdapterWebClient")
  public WebClient ohipAdapterServiceWebClient(WebClient.Builder webClientBuilder,
                                               HttpClient httpClient, JsonMapper objectMapper) {
    return webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(ohipAdapterProperties.getHost())
            .exchangeStrategies(exchangeStrategies(objectMapper))
            .filter(logRequest(log))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
  }

  @Bean
  public HttpClient httpClient(
          @Value("${config.httpClient.connectionTimeout}") Integer connectionTimeout,
          @Value("${config.httpClient.responseTimeout}") Long responseTimeout) {
    return HttpClient.create(connectionProvider())
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .responseTimeout(Duration.ofSeconds(responseTimeout))
            .keepAlive(true)
            .doOnRequest(
                    (request, connection) -> connection.addHandlerFirst(new WebClientLoggingHandler()));
  }

  @Bean
  public ConnectionProvider connectionProvider() {
    return ConnectionProvider.builder("contentEntityConnectionProvider")
            .metrics(false)
            .maxIdleTime(Duration.ofSeconds(30))
            .maxConnections(500)
            .build();
  }
}
