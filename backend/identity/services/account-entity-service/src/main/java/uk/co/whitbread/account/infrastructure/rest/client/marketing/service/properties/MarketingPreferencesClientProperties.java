package uk.co.whitbread.account.infrastructure.rest.client.marketing.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.marketing")
public class MarketingPreferencesClientProperties {
  private String host;
  private String newsletterPreferencesEndpoint;
  private String internalNewsletterPreferencesEndpoint;
  private String confirmDoubleOptInEndpoint;
  private String azureFDID;
  private String apimSubscriptionKey;
}
