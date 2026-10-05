package uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cdh.api.oauth")
public class CdhApiOauthProperties {

  private String subscriptionKeyHeaderName;
  private String bookingSubscriptionKey;
  private String accountSubscriptionKey;
  private String innBusinessSubscriptionKey;
}
