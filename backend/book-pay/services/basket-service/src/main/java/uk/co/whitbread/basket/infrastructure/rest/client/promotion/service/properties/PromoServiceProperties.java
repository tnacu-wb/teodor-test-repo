package uk.co.whitbread.basket.infrastructure.rest.client.promotion.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.promotion")
public class PromoServiceProperties {

  private String host;
  private String promoKindEndpoint;
  private String redeemEndpoint;
}
