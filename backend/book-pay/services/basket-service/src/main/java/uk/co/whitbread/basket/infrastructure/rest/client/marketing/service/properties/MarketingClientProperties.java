package uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.marketing")
public class MarketingClientProperties {

  private String host;
  private String marketingNewsletterEndpoint;
  private String defaultBrandCode;
  private String defaultLanguage;
  private String defaultContactType;
  private String defaultJourney;
  private String defaultChannel;

}
