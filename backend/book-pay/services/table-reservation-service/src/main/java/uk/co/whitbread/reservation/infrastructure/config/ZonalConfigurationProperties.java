package uk.co.whitbread.reservation.infrastructure.config;

import java.time.Duration;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@ConfigurationProperties(prefix = "zonal")
@Data
@Configuration
public class ZonalConfigurationProperties {

  private static final Logger log = LoggerFactory.getLogger(ZonalConfigurationProperties.class);
  private String username;
  private String password;

  private String baseUri;

  private String checkUri;

  private String occasionsUri;

  private String slotsUri;

  private String outletsUri;

  private String eventsUri;

  private String enquiriesUri;

  private String menusUri;

  private long connectTimeout = 10000;

  private long readTimeout = 30;

  @Bean("zonalRestClient")
  public RestClient zonalRestClient(RestClient.Builder restClientBuilder) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofMillis(connectTimeout));
    factory.setReadTimeout(Duration.ofSeconds(readTimeout));
    return restClientBuilder
        .requestFactory(factory)
        .defaultHeaders(headers -> headers.setBasicAuth(username, password))
        .build();
  }
}