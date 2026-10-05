package uk.co.whitbread.reservation.infrastructure.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@ConfigurationProperties(prefix = "aem")
@Data
@Configuration
public class AemConfigurationProperties {

  private String username;
  private String password;
  private String baseUri;
  private String cookieConsentUri;
  private String bookPageContentUri;
  private String footerUri;
  private String headerUri;
  private String zonalUuidUri;
  private String unbrandedRestaurantsUri;
  private String labelUri;

  private long connectTimeout = 10000;
  private long readTimeout = 30;

  @Bean("aemRestClient")
  public RestClient aemRestClient(RestClient.Builder restClientBuilder) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(Duration.ofMillis(connectTimeout));
    factory.setReadTimeout(Duration.ofSeconds(readTimeout));
    return restClientBuilder
        .requestFactory(factory)
        .defaultHeaders(headers -> headers.setBasicAuth(username, password))
        .build();
  }
}
