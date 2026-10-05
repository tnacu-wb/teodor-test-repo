package uk.co.whitbread.content.infrastructure.config;

import static uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils.exchangeStrategies;
import static uk.co.whitbread.content.infrastructure.rest.client.utils.WebClientUtils.logRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.HotelReviewProperties;
import uk.co.whitbread.content.infrastructure.rest.client.ohip.properties.OhipAdapterProperties;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.properties.SnowdropProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {

  private final AemProperties aemProperties;
  private final SnowdropProperties snowdropProperties;
  private final HotelReviewProperties hotelReviewProperties;
  private final OhipAdapterProperties ohipAdapterProperties;
  private final ObjectMapper objectMapper;

  @Bean(name = "hotelReviewWebClient")
  public WebClient hotelReviewClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(hotelReviewProperties.getHost())
        .exchangeStrategies(exchangeStrategies(objectMapper))
        .filter(logRequest(log))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "aemWebClient")
  public WebClient aemWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
    Builder webClBuilder = webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(aemProperties.getHost())
        .exchangeStrategies(exchangeStrategies(objectMapper))
        .filter(logRequest(log))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    if (Boolean.FALSE.equals(aemProperties.getIsProduction())) {
      webClBuilder.defaultHeaders(
          header -> header.setBasicAuth(aemProperties.getUsername(), aemProperties.getPassword()));
    }
    return webClBuilder.build();
  }


  @Bean("ohipAdapterWebClient")
  public WebClient ohipAdapterServiceWebClient(WebClient.Builder webClientBuilder,
      HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(ohipAdapterProperties.getHost())
        .exchangeStrategies(exchangeStrategies(objectMapper))
        .filter(logRequest(log))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "snowdropWebClient")
  public WebClient snowdropWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(snowdropProperties.getHost())
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
        //connection timeout is a period within which a connection between a client and a server must be established
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
        //TCP check probes when the connection is idle
        .option(ChannelOption.SO_KEEPALIVE, true)
        //response timeout is the time we wait to receive a response after sending a request
        .responseTimeout(Duration.ofSeconds(responseTimeout))
        //Keep-Alive support for the outgoing request
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
