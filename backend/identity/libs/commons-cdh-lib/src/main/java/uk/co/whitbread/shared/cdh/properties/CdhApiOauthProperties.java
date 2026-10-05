package uk.co.whitbread.shared.cdh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cdh.api.oauth")
public class CdhApiOauthProperties implements CustomerDataHubProperties {

  private String subscriptionKey;
  private String bookingSubscriptionKey;
}
