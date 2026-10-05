package uk.co.whitbread.review.infrastructure.rest.client.config;

import static uk.co.whitbread.review.infrastructure.rest.utils.WebClientUtils.logRequest;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.review.infrastructure.rest.client.config.component.MetricsFilterFunction;
import uk.co.whitbread.review.infrastructure.rest.client.review.properties.TripAdvisorProperties;

@Data
@Slf4j
@Configuration
public class WebClientConfig {
  final int size = 16 * 1024 * 1024;
  private final TripAdvisorProperties tripAdvisorProperties;
  private final JsonMapper jacksonJsonMapper;

  @Bean(name = "tripAdvisorWebClient")
  public WebClient tripAdvisorWebClient(WebClient.Builder webClientBuilder) {
    var provider = ConnectionProvider.builder("custom")
        .maxIdleTime(Duration.ofSeconds(30))
        .maxConnections(500)
        .build();
    var httpClient = HttpClient.create(provider);
    httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
        Integer.decode(tripAdvisorProperties.getConnectionTimeout()));
    httpClient.responseTimeout(Duration.ofMillis(Long.decode(tripAdvisorProperties.getReadTimeout())));
    var connector = new ReactorClientHttpConnector(httpClient);
    return createWebClient(connector, webClientBuilder);
  }

  private WebClient createWebClient(ReactorClientHttpConnector connector, WebClient.Builder webClientBuilder) {
    WebClient.Builder webClBuilder = webClientBuilder
        .clientConnector(connector)
        .baseUrl(tripAdvisorProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest(log))
        .filter(new MetricsFilterFunction(Boolean.TRUE.equals(tripAdvisorProperties.getWebclientMetricsEnabled())))
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return webClBuilder.build();
  }

  private void configureCodecs(CodecConfigurer configurer, JsonMapper jsonMapper) {
    JsonMapper configuredMapper = jsonMapper.rebuild()
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
    configurer.registerDefaults(false);

    configurer.customCodecs()
        .register(new JacksonJsonEncoder(configuredMapper, MediaType.valueOf("application/json")));
    configurer.customCodecs()
        .register(new JacksonJsonDecoder(configuredMapper, MediaType.valueOf("text/javascript;charset=UTF-8")));
  }

}
