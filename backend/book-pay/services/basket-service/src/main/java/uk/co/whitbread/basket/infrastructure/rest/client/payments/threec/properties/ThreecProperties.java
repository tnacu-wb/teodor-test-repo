package uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.threec")
public class ThreecProperties {

  private String host;
  private String paymentsEndpoint;
  private String tokensEndpoint;
}
