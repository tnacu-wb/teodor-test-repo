package uk.co.whitbread.review.infrastructure.rest.client.review.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.tripadvisor")
public class TripAdvisorProperties {

  private String host;
  private String apiKey;
  private String dataUrl;
  private String reviewsUrl;
  private String connectionTimeout;
  private String readTimeout;
  private Boolean isProduction = false;
  private Boolean webclientMetricsEnabled = false;


}
